package com.renato.vanguard_api.model.card;

import jakarta.persistence.Entity;

@Entity
public class Unit extends Card {
    private String power;
    private String shield;
    private String critical;

    public Unit() {
        super();
    }

    public Unit(String cardIds, String name, String grade, String specialIcon, String flavourText, String nation,
            String clan, String type, String format, String cardEffect, String source, String imageSource,
            String rarities, String artist, String power, String shield, String critical) {
        super(cardIds, name, grade, specialIcon, flavourText, nation, clan, type, format, cardEffect, source,
                imageSource,
                rarities, artist);
        this.power = power;
        this.shield = shield;
        this.critical = critical;
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

}
