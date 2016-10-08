package com.ygo.game.Types;

public enum ZoneType {
    MONSTER(0), SPELL_TRAP(1), DECK(2), GRAVEYARD(3), EXTRA_DECK(4), FIELD_SPELL(5), PENDULUM(6), BANISHED(7);

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
