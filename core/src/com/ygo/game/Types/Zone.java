package com.ygo.game.Types;

public enum Zone {
    MONSTER(0), SPELL_TRAP(1), DECK(2), GRAVEYARD(3), EXTRA_DECK(4), FIELD_SPELL(5), PENDULUM(6), BANISHED(7);

    public final int index;

    Zone(int index) {
        this.index = index;
    }

    public static Zone indexToZone(int index) {
        for (Zone z : Zone.values()) {
            if (z.index == index) {
                return z;
            }
        }
        return null;
    }
}
