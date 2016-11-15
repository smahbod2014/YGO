package com.ygo.game.Types;

import com.ygo.game.Card;

/**
 * Not a very well-chosen class name, but nonetheless describes the position the card is on the field
 */
public class CardPlayMode {
    public static final int FACE_UP = 1;
    public static final int FACE_DOWN = 2;
    public static final int ATTACK_MODE = 4;
    public static final int DEFENSE_MODE = 8;

    public static int createFaceUpAttackMode() {
        return FACE_UP | ATTACK_MODE;
    }

    public static void setFaceUp(Card card) {
        card.playMode &= ~FACE_DOWN;
        card.playMode |= FACE_UP;
    }

    public static boolean isFaceDown(int mode) {
        return (mode & FACE_DOWN) != 0;
    }

    public static boolean isFaceDownDefense(int mode) {
        return (mode & (FACE_DOWN | DEFENSE_MODE)) == (FACE_DOWN | DEFENSE_MODE);
    }

    public static boolean isAttackMode(int mode) {
        return (mode & ATTACK_MODE) != 0;
    }

    public static boolean isDefenseMode(Card card) {
         return (card.playMode & DEFENSE_MODE) != 0;
    }
}
