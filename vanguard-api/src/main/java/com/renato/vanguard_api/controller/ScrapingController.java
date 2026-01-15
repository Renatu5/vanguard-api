package com.renato.vanguard_api.controller;

import com.renato.vanguard_api.services.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scraping")
public class ScrapingController {

    @Autowired
    private ScrapingService scrapingService;

    /**
     * Endpoint para acionar manualmente o processo de scraping.
     * Ao fazer um POST para /api/scraping/trigger, este método será chamado.
     */
    @PostMapping("/trigger")
    public ResponseEntity<String> triggerScraping() {
        try {
            // Chama o serviço que faz a raspagem de dados
            scrapingService.scrapeAndSaveCards();
            return ResponseEntity.ok("Processo de scraping iniciado com sucesso.");
        } catch (Exception e) {
            // Em caso de erro no processo, retorna uma resposta de erro.
            return ResponseEntity.internalServerError().body("Falha ao iniciar o scraping: " + e.getMessage());
        }
    }
}
