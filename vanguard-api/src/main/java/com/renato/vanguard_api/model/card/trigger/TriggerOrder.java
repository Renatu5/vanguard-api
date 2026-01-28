package com.renato.vanguard_api.model.card.trigger;

import java.util.List;

import com.renato.vanguard_api.model.card.Unit;

import jakarta.persistence.Entity;

@Entity
public class TriggerOrder extends Unit {
    private String triggerEffect;

    public TriggerOrder() {
        super();
    }

    public TriggerOrder(List<String> cardsIds, String name, String grade, String specialIcon, String flavourText,
            String nation, String race, String clan, String type, String format, String cardEffect, String source,
            String imageSource,
            List<String> rarities, String artist, String ability, String power, String shield, String critical,
            String triggerEffect) {
        super(cardsIds, name, grade, specialIcon, flavourText, nation, race, clan, type, format, cardEffect, source,
                imageSource,
                rarities, artist, ability, power, shield, critical);
        this.triggerEffect = triggerEffect;
    }

    public String getTriggerEffect() {
        return triggerEffect;
    }

}
