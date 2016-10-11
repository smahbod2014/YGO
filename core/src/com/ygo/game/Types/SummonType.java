package com.ygo.game.Types;

public enum SummonType {
    NORMAL_SUMMON(0), SPECIAL_SUMMON(1), SET(2);

    public int index;

    SummonType(int index) {
        this.index = index;
    }

    public static SummonType indexToLocation(int index) {
        for (SummonType loc : SummonType.values()) {
            if (loc.index == index) {
                return loc;
            }
        }
        return null;
    }
}
