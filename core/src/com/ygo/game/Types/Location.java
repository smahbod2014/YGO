package com.ygo.game.Types;

public enum Location {
    HAND(0), FIELD(1), DECK(2), GRAVEYARD(3);

    public int index;

    Location(int index) {
        this.index = index;
    }

    public static Location indexToLocation(int index) {
        for (Location loc : Location.values()) {
            if (loc.index == index) {
                return loc;
            }
        }
        return null;
    }
}
