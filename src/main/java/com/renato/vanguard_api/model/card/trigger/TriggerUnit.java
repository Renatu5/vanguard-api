package com.renato.vanguard_api.model.card.trigger;

import com.renato.vanguard_api.model.card.Unit;

import jakarta.persistence.Entity;

@Entity
public class TriggerUnit extends Unit {
    private String triggerEffect;

    public TriggerUnit() {
        super();
    }

    public TriggerUnit(TriggerUnit triggerUnit) {
        super(triggerUnit.getCardIds(), triggerUnit.getName(), triggerUnit.getGrade(), triggerUnit.getSpecialIcon(),
                triggerUnit.getFlavorText(), triggerUnit.getNation(), triggerUnit.getRace(), triggerUnit.getClan(),
                triggerUnit.getType(),
                triggerUnit.getFormat(), triggerUnit.getCardEffect(), triggerUnit.getSource(),
                triggerUnit.getImageSource(),
                triggerUnit.getRarities(), triggerUnit.getArtist(), triggerUnit.getAbility(),
                triggerUnit.getPower(), triggerUnit.getShield(), triggerUnit.getCritical());
        this.triggerEffect = triggerUnit.getTriggerEffect();
    }

    public TriggerUnit(String cardsIds, String name, String grade, String specialIcon, String flavourText,
            String nation, String race, String clan, String type, String format, String cardEffect, String source,
            String imageSource,
            String rarities, String artist, String ability, String power, String shield, String critical,
            String triggeEffect, String triggerEffect) {
        super(cardsIds, name, grade, specialIcon, flavourText, nation, race, clan, type, format, cardEffect, source,
                imageSource,
                rarities, artist, ability, power, shield, critical);
        this.triggerEffect = triggerEffect;
    }

    public String getTriggerEffect() {
        return triggerEffect;
    }

    public void setTriggerEffect(String triggerEffect) {
        this.triggerEffect = triggerEffect;
    }

}
