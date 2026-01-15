package com.renato.vanguard_api.model.card.trigger;

import com.renato.vanguard_api.model.card.Unit;

public class TriggerOrder extends Unit {
    private String triggerEffect;

    public TriggerOrder() {
        super();
    }

    public TriggerOrder(String cardsIds, String name, String grade, String specialIcon, String flavourText,
            String nation, String clan, String type, String format, String cardEffect, String source,
            String imageSource,
            String rarities, String artist, String power, String shield, String critical,
            String triggerEffect) {
        super(cardsIds, name, grade, specialIcon, flavourText, nation, clan, type, format, cardEffect, source,
                imageSource,
                rarities, artist, power, shield, critical);
        this.triggerEffect = triggerEffect;
    }

    public String getTriggerEffect() {
        return triggerEffect;
    }

}
