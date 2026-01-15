package com.renato.vanguard_api.model.card;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private ArrayList<String> cardIds = new ArrayList<>();
    private String name;
    private String grade;
    private String specialIcon;
    private String flavourText;
    private String nation;
    private String clan;
    private String type;
    private String format;
    @Column(columnDefinition = "TEXT")
    private String cardEffect;
    private String source;
    private String imageSource;
    private ArrayList<String> rarities = new ArrayList<>();
    private String artist;

    public Card() {
    }

    public Card(String cardIds, String name, String grade, String specialIcon, String flavourText, String nation,
            String clan, String type, String format, String cardEffect, String source, String imageSource,
            String rarities, String artist) {
        this.cardIds.add(cardIds);
        this.name = name;
        this.grade = grade;
        this.specialIcon = specialIcon;
        this.flavourText = flavourText;
        this.nation = nation;
        this.clan = clan;
        this.type = type;
        this.format = format;
        this.cardEffect = cardEffect;
        this.source = source;
        this.imageSource = imageSource;
        this.rarities.add(rarities);
        this.artist = artist;
    }

    public Long getId() {
        return id;
    }

    public List<String> getCardIds() {
        return cardIds;
    }

    public String getName() {
        return name;
    }

    public String getGrade() {
        return grade;
    }

    public String getSpecialIcon() {
        return specialIcon;
    }

    public String getFlavourText() {
        return flavourText;
    }

    public String getNation() {
        return nation;
    }

    public String getClan() {
        return clan;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    public String getCardEffect() {
        return cardEffect;
    }

    public String getSource() {
        return source;
    }

    public String getImageSource() {
        return imageSource;
    }

    public List<String> getRarities() {
        return rarities;
    }

    public String getArtist() {
        return artist;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Card other = (Card) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
