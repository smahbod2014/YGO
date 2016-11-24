package com.ygo.game.db;

import com.ygo.game.Types.Attribute;
import com.ygo.game.Types.CardFlavor;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Race;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CardDefinition {
    private final String name;
    private final String serial;
    private final int atk;
    private final int def;
    private final int level;
    private final Attribute attribute;
    private final Race race;
    private final CardType type;
    private final List<CardFlavor> flavors;
    private final String text;

    public CardDefinition(String name, String serial, int atk, int def, int level, Attribute attribute, Race race, CardType type, String flavors, String text) {
        this.name = name;
        this.serial = serial;
        this.atk = atk;
        this.def = def;
        this.level = level;
        this.attribute = attribute;
        this.race = race;
        this.type = type;
        this.flavors = Arrays.stream(flavors.split("\\s*,\\s*")).map(CardFlavor::toEnum).collect(Collectors.toList());
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getSerial() {
        return serial;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public int getLevel() {
        return level;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Race getRace() {
        return race;
    }

    public CardType getType() {
        return type;
    }

    public List<CardFlavor> getFlavors() {
        return flavors;
    }

    public String getText() {
        return text;
    }
}
