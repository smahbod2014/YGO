package com.ygo.game.Types;

/**
 * Created by semahbod on 10/3/16.
 */
public enum PlayerType {
    CURRENT_PLAYER(0), OPPONENT_PLAYER(1);

    public final int index;

    PlayerType(int index) {
        this.index = index;
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
