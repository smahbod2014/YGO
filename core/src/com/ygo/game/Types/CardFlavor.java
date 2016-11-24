package com.ygo.game.Types;

import java.util.Arrays;

public enum CardFlavor {
    Normal, Effect, Equip, FieldSpell("Field");

    private final String flavorName;

    CardFlavor() {
        this.flavorName = name();
    }

    CardFlavor(String flavorName) {
        this.flavorName = flavorName;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public static CardFlavor toEnum(String flavorName) {
        return Arrays.stream(values()).filter(f -> f.flavorName.equals(flavorName)).findFirst().orElse(null);
    }
}
