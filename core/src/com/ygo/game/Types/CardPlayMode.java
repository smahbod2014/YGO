package com.ygo.game.Types;

/**
 * Not a very well-chosen class name, but nonetheless describes the position the card is on the field
 */
public class CardPlayMode {
    public static final int FACE_UP = 1;
    public static final int FACE_DOWN = 2;
    public static final int ATTACK_MODE = 4;
    public static final int DEFENSE_MODE = 8;

    public static boolean isFaceDown(int mode) {
        return (mode & FACE_DOWN) != 0;
    }

    public static boolean isFaceDownDefense(int mode) {
        return (mode & (FACE_DOWN | DEFENSE_MODE)) == (FACE_DOWN | DEFENSE_MODE);
    }

}
