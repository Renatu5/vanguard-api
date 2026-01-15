package com.renato.vanguard_api.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.renato.vanguard_api.model.card.Card;
import com.renato.vanguard_api.model.card.Unit;
import com.renato.vanguard_api.repository.CardRepository;

@Service
public class ScrapingService {
    private final CardRepository cardRepository;

    public ScrapingService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void scrapeAndSaveCards() {
        String url = "";
        try {
            Document document = Jsoup.connect(url)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();
            // System.out.println("DOCUMENTO: \n" + document);
            String name = document.selectFirst(".name").text();
            Elements cardElements = document.select(".text-list");
            System.out.println("ELEMENTOS DA CARTA: \n" + cardElements);
            Card card;
            // for (Element cardElement : cardElements) {
            // System.out.println(cardElement);
            String type = cardElements.selectFirst(".type").text();
            String nation = cardElements.selectFirst(".nation").text();
            // String group = cardElements.selectFirst(".group").text();
            String race = cardElements.selectFirst(".race").text();
            String grade = cardElements.selectFirst(".grade").text();
            String power = cardElements.selectFirst(".power").text();
            String shield = cardElements.selectFirst(".shield").text();
            String critical = cardElements.selectFirst(".critical").text();
            // String skill = cardElements.selectFirst(".skill").text().split(",")[0];
            String specialIcon = cardElements.selectFirst(".skill").text().split(",")[1].trim();
            System.out.println(specialIcon);
            String cardEffect = document.selectFirst(".effect").text();
            String flavourText = document.selectFirst(".flavor").text();
            String regulation = document.selectFirst(".regulation").text();
            String number = document.selectFirst(".number").text();
            // String illustrator = document.selectFirst(".illustrator").text();
            // String rarity = document.selectFirst(".rarity").text();
            card = new Unit(number, name, grade, specialIcon, flavourText, nation, race, type, regulation,
                    cardEffect, url, url, "RRR", "illustrator", power, shield, critical);
            cardRepository.save(card);
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
