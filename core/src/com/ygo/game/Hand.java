package com.ygo.game;

import com.badlogic.gdx.utils.Array;

public class Hand {
    public static final int CARD_LIMIT = 7;

    private Array<Card> cards = new Array<Card>();

    public Card getCard(int position) {
        return cards.get(position);
    }
}
