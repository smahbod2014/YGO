package com.ygo.game.Types;

import com.badlogic.gdx.Gdx;
import com.ygo.game.Card;

import java.util.Arrays;

/**
 * Not a very well-chosen class name, but nonetheless describes the position the card is on the field
 */
public class CardPlayMode {
    public static final int NONE = 0;
    public static final int FACE_UP = 1;
    public static final int FACE_DOWN = 2;
    public static final int ATTACK_MODE = 4;
    public static final int DEFENSE_MODE = 8;

    public static final CardPlayMode FACE_UP_ATTACK = new CardPlayMode(FACE_UP | ATTACK_MODE);

    private int playMode;

    public CardPlayMode(int... modes) {
        this.playMode = Arrays.stream(modes).reduce(NONE, (a, b) -> a | b);
    }

    public CardPlayMode(CardPlayMode mode) {
        this.playMode = mode.playMode;
    }

    public void changeMode(int mode) {
        switch (mode) {
            case FACE_UP:
                playMode &= ~FACE_DOWN;
                break;
            case FACE_DOWN:
                playMode &= ~FACE_UP;
                break;
            case ATTACK_MODE:
                playMode &= ~DEFENSE_MODE;
                break;
            case DEFENSE_MODE:
                playMode &= ~ATTACK_MODE;
                break;
            default:
                Gdx.app.error(getClass().getSimpleName(), "Only set to one mode at a time");
        }
        playMode |= mode;
    }

    @Deprecated
    public static void setFaceUp(Card card) {
//        card.playMode &= ~FACE_DOWN;
//        card.playMode |= FACE_UP;
    }

    public boolean isFaceDown() {
        return (playMode & FACE_DOWN) != 0;
    }

    public boolean isFaceDownDefense() {
        return (playMode & (FACE_DOWN | DEFENSE_MODE)) == (FACE_DOWN | DEFENSE_MODE);
    }

    public boolean isAttackMode() {
        return (playMode & ATTACK_MODE) != 0;
    }

    public boolean isDefenseMode() {
        return (playMode & DEFENSE_MODE) != 0;
    }

    public int getPlayMode() {
        return playMode;
    }

    public CardPlayMode getOpposite() {
        if (isAttackMode()) {
            return new CardPlayMode(FACE_UP | DEFENSE_MODE);
        }
        else {
            return new CardPlayMode(FACE_UP | ATTACK_MODE);
        }
    }
}
