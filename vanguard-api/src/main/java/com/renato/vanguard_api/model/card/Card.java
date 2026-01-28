package com.renato.vanguard_api.model.card;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ElementCollection
    @CollectionTable(name = "card_ids", joinColumns = @JoinColumn(name = "card_db_id"))
    @Column(name = "card_id")
    private List<String> cardIds;
    private String name;
    private String grade;
    private String specialIcon;
    private String flavorText;
    private String nation;
    private String race;
    private String clan;
    private String type;
    private String format;
    @Column(columnDefinition = "TEXT")
    private String cardEffect;
    private String source;
    private String imageSource;
    @ElementCollection
    @CollectionTable(name = "card_rarities", joinColumns = @JoinColumn(name = "card_db_id"))
    @Column(name = "rarity")
    private List<String> rarities;
    private String artist;

    public Card() {
    }

    public Card(List<String> cardIds, String name, String grade, String specialIcon, String flavourText, String nation,
            String race, String clan, String type, String format, String cardEffect, String source, String imageSource,
            List<String> rarities, String artist) {
        this.cardIds = cardIds;
        this.name = name;
        this.grade = grade;
        this.specialIcon = specialIcon;
        this.flavorText = flavourText;
        this.nation = nation;
        this.race = race;
        this.clan = clan;
        this.type = type;
        this.format = format;
        this.cardEffect = cardEffect;
        this.source = source;
        this.imageSource = imageSource;
        this.rarities = rarities;
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

    public String getFlavorText() {
        return flavorText;
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

    public String getRace() {
        return race;
    }

}
