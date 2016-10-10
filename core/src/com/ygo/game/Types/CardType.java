package com.ygo.game.Types;

public class CardType {
    public static final int MONSTER = 1;
    public static final int SPELL = 2;
    public static final int TRAP = 4;
    public static final int NORMAL_MONSTER = 8;
    public static final int EFFECT_MONSTER = 16;
    public static final int NORMAL_SPELL = 32;
    public static final int FIELD_SPELL = 64;
    public static final int EQUIP_SPELL = 128;
    public static final int NORMAL_TRAP = 256;

    public static int normalMonster() {
        return MONSTER | NORMAL_MONSTER;
    }

    public static int effectMonster() {
        return MONSTER | EFFECT_MONSTER;
    }

    public static int normalSpell() {
        return SPELL | NORMAL_SPELL;
    }

    public static int equipSpell() {
        return SPELL | EQUIP_SPELL;
    }

    public static int fieldSpell() {
        return SPELL | FIELD_SPELL;
    }

    public static int normalTrap() {
        return TRAP | NORMAL_TRAP;
    }
}
