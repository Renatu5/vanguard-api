package com.renato.vanguard_api.model.card;

import jakarta.persistence.Entity;

@Entity
public class Unit extends Card {
    private String ability;
    private String power;
    private String shield;
    private String critical;

    public Unit() {
        super();
    }

    /**
     * Constructor to create a new basic Unit object. This type, beside of Trigger,
     * represent the majority of the cards in the game.
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
     * @param ability
     * @param power
     * @param shield
     * @param critical
     */

    public Unit(String cardIds, String name, String grade, String specialIcon, String flavourText, String nation,
            String race,
            String clan, String type, String format, String cardEffect, String source, String imageSource,
            String rarities, String artist, String ability, String power, String shield, String critical) {
        super(cardIds, name, grade, specialIcon, flavourText, nation, race, clan, type, format, cardEffect, source,
                imageSource,
                rarities, artist);
        this.ability = ability;
        this.power = power;
        this.shield = shield;
        this.critical = critical;
    }

    public Unit(Unit unit) {
        super(unit.getCardIds(), unit.getName(), unit.getGrade(), unit.getSpecialIcon(), unit.getFlavorText(),
                unit.getNation(), unit.getRace(), unit.getClan(), unit.getType(), unit.getFormat(),
                unit.getCardEffect(), unit.getSource(), unit.getImageSource(), unit.getRarities(), unit.getArtist());
        this.ability = unit.getAbility();
        this.power = unit.getPower();
        this.shield = unit.getShield();
        this.critical = unit.getCritical();
    }

    public String getPower() {
        return power;
    }

    public String getShield() {
        return shield;
    }

    public String getCritical() {
        return critical;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public void setShield(String shield) {
        this.shield = shield;
    }

    public void setCritical(String critical) {
        this.critical = critical;
    }

}
