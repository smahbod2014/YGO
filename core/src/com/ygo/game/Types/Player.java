package com.ygo.game.Types;

/**
 * Created by semahbod on 10/3/16.
 */
public enum Player {
    PLAYER_1(0), PLAYER_2(1);

    @Deprecated
    public final int index;

    Player(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Player " + (this.index + 1);
    }

    public Player getOpponent() {
        return this == PLAYER_1 ? PLAYER_2 : PLAYER_1;
    }

    public static Player indexToPlayer(int index) {
        for (Player p : Player.values()) {
            if (p.index == index) {
                return p;
            }
        }
        return null;
    }
}
