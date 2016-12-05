package com.ygo.game.Types;

public enum Location {
    Hand(0), Field(1), Deck(2), Graveyard(3);

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
