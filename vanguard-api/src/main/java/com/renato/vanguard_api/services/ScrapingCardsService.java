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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

            // PHASE 1: Get all individual card URLs from all product pages.
            List<String> allCardUrls = getAllCardUrls();
            logger.info("Found {} card URLs to scrape.", allCardUrls.size());

            if (allCardUrls.isEmpty()) {
                logger.warn("No card URLs found. Aborting scraping process.");
                return;
            }

            // PHASE 2: Scrape card details in parallel.
            // Create a list of asynchronous tasks. Each task scrapes one card.

            List<CompletableFuture<Optional<Card>>> futures = allCardUrls.stream()
                    .map(url -> CompletableFuture.supplyAsync(() -> scrapeCardDetails(url), executor))
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
                    .filter(Optional::isPresent)
                    .map(Optional::get)
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
     * Logs a scraping error to a dedicated file. This method is synchronized to
     * handle concurrent writes from multiple threads.
     *
     * @param cardUrl The URL that failed to scrape.
     * @param reason  A description of why it failed.
     * @param e       The exception that was caught, can be null.
     */
    private synchronized void logScrapingError(String cardUrl, String reason, Exception e) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date());
        // Using try-with-resources to ensure the writer is closed.
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("scraping-errors.log", true)))) {
            out.println("Timestamp: " + timestamp);
            out.println("URL: " + cardUrl);
            out.println("Reason: " + reason);
            if (e != null) {
                // Print the full stack trace to the file for detailed debugging.
                e.printStackTrace(out);
            }
            out.println("--------------------------------------------------");
        } catch (IOException ioEx) {
            logger.error("CRITICAL: Could not write to scraping-errors.log file.", ioEx);
        }
    }

    /**
     * Fetches all product pages from the database, then scrapes each page to find
     * individual card URLs.
     * 
     * @return A list of all card URLs.
     */
    private List<String> getAllCardUrls() {
        List<Product> products = productRepository.findAll();
        List<String> cardUrls = new ArrayList<>();

        for (Product product : products) {
            try {
                logger.info("Scraping product page for card links: {}", product.getCardList());
                Document productPage = Jsoup.connect(product.getCardList())
                        .userAgent(
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                        .get();

                // !!! ACTION REQUIRED !!!
                // You need to provide the correct CSS selector to find the links to the
                // individual cards.
                // Example selector: "div.card-list-container a.card-link"
                // Replace "a.card-link-selector" with the actual selector from the website.
                var cardLinkElements = productPage.select(".cardlist_gallerylist ul li a");

                for (var linkElement : cardLinkElements) {
                    cardUrls.add(baseUrl + linkElement.attr("href"));
                }
            } catch (IOException e) {
                logger.error("Failed to scrape product page: {}", product.getCardList(), e);
            } catch (Exception e) {
                logger.warn("Product URL scraping was interrupted. Returning partial list of card URLs.");
                Thread.currentThread().interrupt();
                break; // Sai do loop e retorna o que foi coletado até agora.
            }
        }
        return cardUrls;
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
            Optional<Card> cardOptional = scrapeCardDetails(selectedCardUrl);

            System.out.println(cardOptional.toString());
            // Salva a carta se o scraping foi bem-sucedido
            cardOptional.ifPresent(cardRepository::save);

        } catch (IOException e) {
            logger.error("Falha ao obter a lista de cartas da página do produto: {}", product.getCardList(), e);
        } catch (NullPointerException e) {
            logger.error("Não foi possível encontrar o link da carta na página do produto: {}", product.getCardList(),
                    e);
        }

    }

    /**
     * Scrapes a single web page to extract details for one card.
     * 
     * @param cardUrl The URL of the card's detail page.
     * @return An Optional containing the Card object if successful, or an empty
     *         Optional if an error occurs.
     */
    private Optional<Card> scrapeCardDetails(String cardUrl) {
        final int MAX_RETRIES = 3;
        long delayMillis = 2000; // Começa com 2 segundos

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                // Um pequeno delay de "cortesia" antes de cada tentativa.
                Thread.sleep(1000);

                Document doc = Jsoup.connect(cardUrl)
                        .userAgent(
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                        .timeout(30000) // Timeout de 30 segundos
                        .get();

                System.out.println(Jsoup.parseBodyFragment(doc.selectFirst(".face").html()).text());
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
                List<String> cardIds = List.of(doc.selectFirst(".number").text()); // Assuming one ID for now
                List<String> rarities = doc.select(".rarity").stream().map(e -> e.text())
                        .collect(Collectors.toList());
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

                logger.info("Successfully scraped card: {}", name);
                return Optional.of(card); // Retorna a carta se o scraping foi bem-sucedido

            } catch (org.jsoup.HttpStatusException e) {
                // Não retentar em caso de 404 (Not Found), pois a página não existe
                if (e.getStatusCode() == 404) {
                    logger.error("Página não encontrada (404): {}", cardUrl);
                    logScrapingError(cardUrl, "HTTP 404 Not Found", e);
                    return Optional.empty();
                }

                if (attempt < MAX_RETRIES) {
                    logger.warn("Tentativa {}/{}: Erro HTTP {} para {}. Tentando novamente em {}ms...", attempt,
                            MAX_RETRIES, e.getStatusCode(), cardUrl, delayMillis);
                    try {
                        Thread.sleep(delayMillis);
                        delayMillis *= 2; // Backoff exponencial
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("Tarefa de scraping para {} foi interrompida durante o backoff.", cardUrl, ie);
                        return Optional.empty(); // Sai se for interrompido
                    }
                } else {
                    logger.error("Falha ao fazer scraping da URL {} após {} tentativas. Status code final: {}.",
                            cardUrl,
                            attempt, e.getStatusCode(), e);
                    logScrapingError(cardUrl,
                            "HttpStatusException after " + attempt + " attempts. Final status: " + e.getStatusCode(),
                            e);
                    return Optional.empty(); // Para de tentar em outros erros HTTP ou se atingir o máximo
                }
            } catch (IOException e) {
                if (attempt < MAX_RETRIES) {
                    logger.warn("Tentativa {}/{}: Erro de conexão ({}) para {}. Tentando novamente em {}ms...", attempt,
                            MAX_RETRIES, e.getClass().getSimpleName(), cardUrl, delayMillis);
                    try {
                        Thread.sleep(delayMillis);
                        delayMillis *= 2;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return Optional.empty();
                    }
                } else {
                    logger.error("Ocorreu um erro de IO persistente durante o scraping de {}: {}", cardUrl,
                            e.getMessage());
                    logScrapingError(cardUrl, "IOException after " + MAX_RETRIES + " attempts.", e);
                    return Optional.empty(); // Para em outros erros de IO
                }
            } catch (NullPointerException e) {
                logger.error("Falha ao analisar detalhes da carta para {}. Um elemento requerido não foi encontrado.",
                        cardUrl, e);
                logScrapingError(cardUrl, "NullPointerException - A required element was not found on the page.", e);
                return Optional.empty(); // Para se a análise falhar
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Tarefa de scraping para {} foi interrompida.", cardUrl, e);
                logScrapingError(cardUrl, "InterruptedException - The scraping task was interrupted.", e);
                return Optional.empty(); // Sai se for interrompido
            } catch (Exception e) {
                // Captura qualquer outra exceção não prevista (como
                // ArrayIndexOutOfBoundsException)
                logger.error("Erro inesperado (Genérico) ao processar a carta {}: {}", cardUrl, e.getMessage());
                logScrapingError(cardUrl, "Unexpected Exception - " + e.getClass().getSimpleName(), e);
                return Optional.empty(); // Retorna vazio para não quebrar o processo em lote
            }
        }

        logger.error("Falha ao fazer scraping da URL {} após todas as {} tentativas.", cardUrl, MAX_RETRIES);
        logScrapingError(cardUrl, "Failed after all " + MAX_RETRIES + " retries.", null);
        return Optional.empty();
    }
}