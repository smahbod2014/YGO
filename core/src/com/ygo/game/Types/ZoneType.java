package com.ygo.game.Types;

public enum ZoneType {
    MONSTER(0), SPELL_TRAP(1), DECK(2), GRAVEYARD(3);

    public final int index;

    ZoneType(int index) {
        this.index = index;
    }

    public static ZoneType indexToZone(int index) {
        for (ZoneType z : ZoneType.values()) {
            if (z.index == index) {
                return z;
            }
        }
        return null;
    }
}
