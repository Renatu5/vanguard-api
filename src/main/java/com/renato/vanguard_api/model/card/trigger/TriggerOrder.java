package com.renato.vanguard_api.model.card.trigger;

import com.renato.vanguard_api.model.card.Card;

import jakarta.persistence.Entity;

@Entity
public class TriggerOrder extends Card {
    private String triggerEffect;

    public TriggerOrder() {
        super();
    }

    public TriggerOrder(TriggerOrder triggerOrder) {
        super(triggerOrder.getCardIds(), triggerOrder.getName(), triggerOrder.getGrade(), triggerOrder.getSpecialIcon(),
                triggerOrder.getFlavorText(), triggerOrder.getNation(), triggerOrder.getRace(), triggerOrder.getClan(),
                triggerOrder.getType(),
                triggerOrder.getFormat(), triggerOrder.getCardEffect(), triggerOrder.getSource(),
                triggerOrder.getImageSource(),
                triggerOrder.getRarities(), triggerOrder.getArtist());
        this.triggerEffect = triggerOrder.getTriggerEffect();
    }

    public TriggerOrder(String cardsIds, String name, String grade, String specialIcon, String flavourText,
            String nation, String race, String clan, String type, String format, String cardEffect, String source,
            String imageSource,
            String rarities, String artist, String ability, String power, String shield, String critical,
            String triggerEffect) {
        super(cardsIds, name, grade, specialIcon, flavourText, nation, race, clan, type, format, cardEffect, source,
                imageSource,
                rarities, artist);
        this.triggerEffect = triggerEffect;
    }

    public String getTriggerEffect() {
        return triggerEffect;
    }

    public void setTriggerEffect(String triggerEffect) {
        this.triggerEffect = triggerEffect;
    }

}
