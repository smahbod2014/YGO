package com.ygo.game.Types;

/**
 * Created by semahbod on 10/3/16.
 */
public enum Player {
    PLAYER_1(0), PLAYER_2(1);

    public final int index;

    Player(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Player " + (this.index + 1);
    }

    public Player getOpponent() {
        return indexToPlayer((this.index + 1) % 2);
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
