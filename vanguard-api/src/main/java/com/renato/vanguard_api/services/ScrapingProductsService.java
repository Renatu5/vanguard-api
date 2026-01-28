package com.renato.vanguard_api.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.renato.vanguard_api.model.Product;
import com.renato.vanguard_api.repository.ProductRepository;

@Service
public class ScrapingProductsService {
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
                String title = productElements.select(".title").text();
                String releaseDate = productElements.select(".release").text();
                String code = title.substring(title.indexOf("["), title.indexOf("]") + 1);
                Product product = new Product(code, title, productLink, releaseDate);
                System.out.println(product.getName());
                productsToSave.add(product);
            }
            ProductRepository.saveAll(productsToSave);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
