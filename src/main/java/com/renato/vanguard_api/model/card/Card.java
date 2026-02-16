package com.renato.vanguard_api.model.card;

import com.renato.vanguard_api.model.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardIds;
    private String name;
    private String grade;
    private String specialIcon;
    @Column(columnDefinition = "TEXT")
    private String flavorText;
    private String nation;
    private String race;
    private String clan;
    private String type;
    private String format;
    @Column(columnDefinition = "TEXT")
    private String cardEffect;
    @Column(columnDefinition = "TEXT")
    private String source;
    @Column(columnDefinition = "TEXT")
    private String imageSource;
    private String rarities;
    private String artist;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Card() {
    }

    /**
     * Constructor to create the base Card object. this represent any other type of
     * card, which isn't are Unit or a Trigger, like Tokens, Orders, Crests, etc.
     * This constructor should be used for the exceptions, while the Unit or Trigger
     * constructor should be used for the majority of the cards in the game.
     * 
     * @param cardIds
     * @param name
     * @param grade
     * @param specialIcon
     * @param flavourText
     * @param nation
     * @param race
     * @param clan
     * @param type
     * @param format
     * @param cardEffect
     * @param source
     * @param imageSource
     * @param rarities
     * @param artist
     */
    public Card(String cardIds, String name, String grade, String specialIcon, String flavourText, String nation,
            String race, String clan, String type, String format, String cardEffect, String source, String imageSource,
            String rarities, String artist) {
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

    public Card(Card card) {
        this.cardIds = card.getCardIds();
        this.name = card.getName();
        this.grade = card.getGrade();
        this.specialIcon = card.getSpecialIcon();
        this.flavorText = card.getFlavorText();
        this.nation = card.getNation();
        this.race = card.getRace();
        this.clan = card.getClan();
        this.type = card.getType();
        this.format = card.getFormat();
        this.cardEffect = card.getCardEffect();
        this.source = card.getSource();
        this.imageSource = card.getImageSource();
        this.rarities = card.getRarities();
        this.artist = card.getArtist();
    }

    public Long getId() {
        return id;
    }

    public String getCardIds() {
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

    public String getRarities() {
        return rarities;
    }

    public String getArtist() {
        return artist;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public void setCardIds(String cardIds) {
        this.cardIds = cardIds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setSpecialIcon(String specialIcon) {
        this.specialIcon = specialIcon;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void setClan(String clan) {
        this.clan = clan;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setCardEffect(String cardEffect) {
        this.cardEffect = cardEffect;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public void setRarities(String rarities) {
        this.rarities = rarities;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

}
