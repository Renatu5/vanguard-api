package com.renato.vanguard_api.services;

import com.renato.vanguard_api.model.Product;
import com.renato.vanguard_api.model.card.Card;
import com.renato.vanguard_api.model.card.Unit;
import com.renato.vanguard_api.model.card.trigger.TriggerOrder;
import com.renato.vanguard_api.model.card.trigger.TriggerUnit;
import com.renato.vanguard_api.repository.CardRepository;
import com.renato.vanguard_api.repository.ProductRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ScrapingCardsService {

    private static final Logger logger = LoggerFactory.getLogger(ScrapingCardsService.class);
    private final CardRepository cardRepository;
    private final ProductRepository productRepository;
    private final String baseUrl = "https://en.cf-vanguard.com";

    public ScrapingCardsService(CardRepository cardRepository, ProductRepository productRepository) {
        this.cardRepository = cardRepository;
        this.productRepository = productRepository;
    }

    /**
     * Main method to orchestrate the parallel scraping of all cards.
     */
    public void scrapeAndSaveAllCards() {
        // Define the number of concurrent threads. A good starting point is the number
        // of available CPU cores.
        // Adjust this number based on performance and the target server's tolerance.
        int parallelThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(parallelThreads);

        try {
            logger.info("Starting card scraping process...");

            // PHASE 1: Prepare scraping tasks for each product.
            // We get the first card URL for each product to start the chain.
            List<CardScrapeTask> allCardTasks = getAllCardTasks();
            logger.info("Found {} products to scrape.", allCardTasks.size());

            if (allCardTasks.isEmpty()) {
                logger.warn("No products found to scrape. Aborting scraping process.");
                return;
            }

            // PHASE 2: Scrape cards for each product in parallel.
            // Each task processes one product and collects all its cards.

            List<CompletableFuture<List<Card>>> futures = allCardTasks.stream()
                    .map(task -> CompletableFuture.supplyAsync(
                            () -> scrapeProductCards(task.url, task.product),
                            executor))
                    .collect(Collectors.toList());

            // Wait for all the futures to complete.
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join(); // 'join()' waits for completion, similar to 'get()' but without a checked
                          // exception.

            logger.info("All scraping tasks completed. Collecting results...");

            // PHASE 3: Collect results and save to the database.
            // Filter out any cards that failed to scrape (represented as Optional.empty).
            List<Card> successfullyScrapedCards = futures.stream()
                    .map(CompletableFuture::join) // Get the result from each future
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            if (!successfullyScrapedCards.isEmpty()) {
                logger.info("Saving {} successfully scraped cards to the database...", successfullyScrapedCards.size());
                cardRepository.saveAll(successfullyScrapedCards);
                logger.info("All cards saved successfully!");
            } else {
                logger.warn("No cards were successfully scraped.");
            }

        } finally {
            // Always shut down the executor service to release the threads.
            executor.shutdown();
        }
    }

    /**
     * Fetches all product pages from the database, then scrapes each page to find
     * individual card URLs.
     * 
     * @return A list of all card URLs.
     */
    private List<CardScrapeTask> getAllCardTasks() {
        List<Product> products = productRepository.findAll();
        List<CardScrapeTask> tasks = new ArrayList<>();

        for (Product product : products) {
            try {
                logger.info("Scraping product page for card links: {}", product.getCardList());
                Document productPage = Jsoup.connect(product.getCardList())
                        .userAgent(
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                        .get();

                var firstCardLinkElement = productPage.selectFirst(".cardlist_gallerylist ul li a");
                if (firstCardLinkElement != null) {
                    String firstCardLink = baseUrl + firstCardLinkElement.attr("href");
                    int quantityCards = Integer.parseInt(productPage.select(".number").text().split(" ")[0]);
                    product.setQuantity(quantityCards);
                    productRepository.save(product);

                    if (firstCardLink != null) {
                        tasks.add(new CardScrapeTask(firstCardLink, product));
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to scrape product page: {}", product.getCardList(), e);
            }
        }
        return tasks;
    }

    public void scrapeOneCard() {
        Product product = productRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Produto com ID 8 não encontrado"));
        System.out.println(product.getName());
        try {
            Document dc = Jsoup.connect(product.getCardList()).get();
            String selectedCardUrl = baseUrl + dc.select(".cardlist_gallerylist ul li a").get(6).attr("href");
            System.out.println("carta selecionada: " + selectedCardUrl);

            // scrapeCardDetails agora lida com suas próprias exceções e retentativas
            List<Card> cards = scrapeProductCards(selectedCardUrl, product);
            System.out.println(cards.toString());
        } catch (IOException e) {
            logger.error("Falha ao obter a lista de cartas da página do produto: {}", product.getCardList(), e);
        } catch (NullPointerException e) {
            logger.error("Não foi possível encontrar o link da carta na página do produto: {}", product.getCardList(),
                    e);
        }

    }

    /**
     * Scrapes a chain of cards starting from the given URL.
     * Iterates through the "next" links until the end of the product list.
     * 
     * @param startUrl The URL of the first card to scrape.
     * @param product  The product these cards belong to.
     * @return A list of scraped cards.
     */
    private List<Card> scrapeProductCards(String startUrl, Product product) {
        List<Card> cards = new ArrayList<>();
        String currentUrl = startUrl;
        final int MAX_RETRIES = 3;
        long delayMillis = 2000; // Começa com 2 segundos

        while (currentUrl != null && !currentUrl.equals(baseUrl)) {
            Document doc = null;
            boolean success = false;

            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try {
                    // Um pequeno delay de "cortesia" antes de cada tentativa.
                    Thread.sleep(1000);

                    doc = Jsoup.connect(currentUrl)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                            .timeout(30000) // Timeout de 30 segundos
                            .get();
                    success = true;
                    break; // Sucesso, sai do loop de retry
                } catch (org.jsoup.HttpStatusException e) {
                    if (e.getStatusCode() == 404) {
                        logger.error("Página não encontrada (404): {}", currentUrl);
                        break; // Falha fatal para esta carta
                    }
                    if (attempt < MAX_RETRIES) {
                        logger.warn("Tentativa {}/{}: Erro HTTP {} para {}. Tentando novamente em {}ms...", attempt,
                                MAX_RETRIES, e.getStatusCode(), currentUrl, delayMillis);
                        try {
                            Thread.sleep(delayMillis);
                            delayMillis *= 2;
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return cards; // Interrompe tudo
                        }
                    } else {
                        logger.error("Falha final HTTP {} para {}", e.getStatusCode(), currentUrl);
                    }
                } catch (IOException e) {
                    if (attempt < MAX_RETRIES) {
                        logger.warn("Tentativa {}/{}: Erro IO para {}. Tentando novamente...", attempt, MAX_RETRIES,
                                currentUrl);
                        try {
                            Thread.sleep(delayMillis);
                            delayMillis *= 2;
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return cards;
                        }
                    } else {
                        logger.error("Erro IO persistente para {}: {}", currentUrl, e.getMessage());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return cards;
                } catch (Exception e) {
                    logger.error("Erro inesperado ao conectar em {}: {}", currentUrl, e.getMessage());
                    break;
                }
            }

            if (!success || doc == null) {
                logger.error("Não foi possível carregar a página {}, interrompendo a cadeia deste produto.",
                        currentUrl);
                break;
            }

            try {
                String name = Jsoup.parseBodyFragment(doc.selectFirst(".face").html()).text();
                System.out.println(name);
                String type = doc.selectFirst(".type").text();
                System.out.println(type);
                String nation = doc.selectFirst(".nation").text();
                System.out.println(nation);
                String race = doc.selectFirst(".race").text();
                System.out.println(race);
                String clan = doc.selectFirst(".group") != null ? doc.selectFirst(".group").text() : "N/A";
                System.out.println(clan);
                String grade = doc.selectFirst(".grade").text();
                System.out.println(grade);
                String power = doc.selectFirst(".power").text();
                System.out.println(power);
                String critical = doc.selectFirst(".critical").text();
                System.out.println(critical);
                String shield = doc.selectFirst(".shield").text();
                System.out.println(shield);

                // Lógica robusta para extrair 'ability' e 'specialIcon', evitando
                // ArrayIndexOutOfBoundsException.
                String[] skillParts = doc.selectFirst(".skill").text().split(",");
                String ability = "N/A";
                if (skillParts.length > 0 && !skillParts[0].trim().isEmpty()) {
                    ability = skillParts[0].trim();
                }
                System.out.println(ability);
                String specialIcon = "N/A";
                if (skillParts.length > 1 && !skillParts[1].trim().isEmpty()) {
                    specialIcon = skillParts[1].trim();
                }
                System.out.println(specialIcon);
                String flavorText = doc.selectFirst(".flavor").text();
                System.out.println(flavorText);
                String format = doc.selectFirst(".regulation").text();
                System.out.println(format);
                String cardEffect = doc.selectFirst(".effect").text();
                System.out.println(cardEffect);
                String source = doc.baseUri();
                System.out.println(source);
                String imageSource = baseUrl + doc.selectFirst(".image .main img").attr("src");
                String artist = doc.selectFirst(".illstrator").text();
                System.out.println(artist);
                String giftText = doc.selectFirst(".gift").text();
                String triggerEffect = giftText.isEmpty() ? "N/A" : giftText;
                System.out.println(triggerEffect);
                String cardIds = doc.selectFirst(".number").text(); // Assuming one ID for now
                String rarities = doc.select(".rarity").text();
                // Thread.sleep(TimeUnit.MINUTES.toMillis(30));
                Card card;
                if (type.equalsIgnoreCase("Trigger Unit")) {
                    card = new TriggerUnit(cardIds, name, grade, specialIcon, flavorText, nation, race, clan, type,
                            format,
                            cardEffect, source, imageSource, rarities, artist, ability, power, shield, critical,
                            cardEffect,
                            triggerEffect);
                } else if (type.equalsIgnoreCase("Trigger Order")) {
                    card = new TriggerOrder(cardIds, name, grade, specialIcon, flavorText, nation, race, clan, type,
                            format,
                            cardEffect, source, imageSource, rarities, artist, ability, power, shield, critical,
                            triggerEffect);
                } else if (type.equalsIgnoreCase("Normal Unit")) {
                    card = new Unit(cardIds, name, grade, specialIcon, flavorText, nation, race, clan, type, format,
                            cardEffect,
                            source, imageSource, rarities, artist, ability, power, shield, critical);
                } else {
                    card = new Card(cardIds, name, grade, specialIcon, triggerEffect, nation, race, clan, type, format,
                            cardEffect, source, imageSource, rarities, artist);
                }
                card.setProduct(product);
                cards.add(card);
                logger.info("Successfully scraped card: {}", name);

                String nextHref = doc.select("#nextCardLink").attr("href");
                if (nextHref != null && !nextHref.isEmpty()) {
                    currentUrl = baseUrl + nextHref;
                } else {
                    currentUrl = null;
                }
            } catch (Exception e) {
                logger.error("Erro ao analisar dados da carta {}: {}", currentUrl, e.getMessage());
                break; // Interrompe a cadeia se houver erro de parsing
            }
        }
        return cards;
    }

    private static class CardScrapeTask {
        String url;
        Product product;

        CardScrapeTask(String url, Product product) {
            this.url = url;
            this.product = product;
        }
    }
}