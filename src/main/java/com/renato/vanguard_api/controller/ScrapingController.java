package com.renato.vanguard_api.controller;

import com.renato.vanguard_api.services.ScrapingCardsService;
import com.renato.vanguard_api.services.ScrapingProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scraping")
public class ScrapingController {

    private final ScrapingCardsService scrapingCardsService;

    @Autowired
    private ScrapingProductsService scrapingService;

    ScrapingController(ScrapingCardsService scrapingCardsService) {
        this.scrapingCardsService = scrapingCardsService;
    }

    /**
     * Endpoint para acionar manualmente o processo de scraping.
     * Ao fazer um POST para /api/scraping/trigger, este método será chamado.
     */
    @PostMapping("/products")
    public ResponseEntity<String> productsScraping() {
        try {
            // Chama o serviço que faz a raspagem de dados
            scrapingService.scrapeAndSaveProducts();
            return ResponseEntity.ok("Scraping process finished successfully!");
        } catch (Exception e) {
            // Em caso de erro no processo, retorna uma resposta de erro.
            return ResponseEntity.internalServerError().body("Failed to start scraping process: " + e.getMessage());
        }
    }

    @PostMapping("/card")
    public ResponseEntity<String> singleCardScraping() {
        try {
            scrapingCardsService.scrapeOneCard();
            return ResponseEntity.ok("Scraping of one card finished successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to start scraping of one card: " + e.getMessage());
        }
    }

    @PostMapping("/cards")
    public ResponseEntity<String> multipleCardsScraping() {
        try {
            scrapingCardsService.scrapeAndSaveAllCards();
            return ResponseEntity.ok("Scraping of cards finished successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to start scraping of cards: " + e.getMessage());
        }
    }

}
