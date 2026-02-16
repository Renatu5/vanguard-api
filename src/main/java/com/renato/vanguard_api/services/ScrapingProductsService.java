package com.renato.vanguard_api.services;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.renato.vanguard_api.model.Product;
import com.renato.vanguard_api.repository.ProductRepository;

@Service
public class ScrapingProductsService {
    private static final Logger logger = LoggerFactory.getLogger(ScrapingProductsService.class);

    private final ProductRepository ProductRepository;

    public ScrapingProductsService(ProductRepository ProductRepository) {
        this.ProductRepository = ProductRepository;
    }

    private final String baseUrl = "https://en.cf-vanguard.com";

    public void scrapeAndSaveProducts() {

        String url = "https://en.cf-vanguard.com/cardlist/";
        try {
            Document document = Jsoup.connect(url)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();
            List<Product> productsToSave = new ArrayList<>();
            Elements products = document.select(".products-list div a");
            products.removeIf(element -> element.select(".title")
                    .text()
                    .contains("Imaginary Gift") ||
                    element.select(".title").text().contains("Demo Deck"));
            // Elements productElements = document.select(".text");
            products.removeLast();
            for (Element productElements : products) {
                String productLink = baseUrl + productElements.select("a").attr("href");
                String titleContent = productElements.select(".title").text();
                String title = titleContent.substring(titleContent.indexOf("]") + 1).trim();
                String code = titleContent.substring(titleContent.indexOf("[") + 1, titleContent.indexOf("]")).trim();
                String releaseDateString = productElements.select(".release").text();
                LocalDate releaseDate = parseReleaseDate(releaseDateString);
                Product product = new Product(code, title, productLink, releaseDate);
                productsToSave.add(product);
            }
            productsToSave.sort((d1, d2) -> d2.compare(d1));
            ProductRepository.saveAll(productsToSave.reversed());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocalDate parseReleaseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        String cleanDateStr = dateStr.replace("Release Date : ", "").trim();

        // Remove informações entre parênteses e a palavra "Release"
        // Ex: "September 1 (Friday), 2017" -> "September 1 , 2017"
        // Ex: "09/06/2013(Fri) Release" -> "09/06/2013 "
        cleanDateStr = cleanDateStr.replaceAll("\\(.*?\\)", "").replace("Release", "").trim();

        // Remove sufixos ordinais (st, nd, rd, th) de qualquer lugar da string
        cleanDateStr = cleanDateStr.replaceAll("(?<=\\d)(st|nd|rd|th)", "");
        cleanDateStr = cleanDateStr.replace(" ,", ",");

        // Formatter for "MM/dd/yyyy"
        DateTimeFormatter slashFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);

        // Formatter for "Month Day, Year" e.g., "May 24, 2024"
        DateTimeFormatter wordFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

        // Formatter for "Day Month Year" e.g., "7 July 2012"
        DateTimeFormatter dayFirstWordFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

        // September 1 (Friday), 2017
        try {
            if (cleanDateStr.contains("/")) {
                return LocalDate.parse(cleanDateStr, slashFormatter);
            } else {
                try {
                    return LocalDate.parse(cleanDateStr, wordFormatter);
                } catch (DateTimeParseException e) {
                    return LocalDate.parse(cleanDateStr, dayFirstWordFormatter);
                }
            }
        } catch (DateTimeParseException e) {
            logger.error("Could not parse date string: '{}'. It will be set to null.", dateStr, e);
            return null;
        }
    }
}
