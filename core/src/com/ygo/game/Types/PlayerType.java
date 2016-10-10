package com.ygo.game.Types;

/**
 * Created by semahbod on 10/3/16.
 */
public enum PlayerType {
    PLAYER_1(0), PLAYER_2(1);

    public final int index;

    PlayerType(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Player " + (this.index + 1);
    }

    public PlayerType getOpponent() {
        return indexToPlayer((this.index + 1) % 2);
    }

    public static PlayerType indexToPlayer(int index) {
        for (PlayerType p : PlayerType.values()) {
            if (p.index == index) {
                return p;
            }
        }
        return null;
    }
}
