package com.ygo.game.Messages;

import com.badlogic.gdx.utils.Array;
import com.ygo.game.Card;

public class GameInitializationMessage {
    //card ids
    public Array<String> p1Deck, p2Deck;

    public GameInitializationMessage() {

    }

    public GameInitializationMessage(Array<String> p1Deck, Array<String> p2Deck) {
        this.p1Deck = p1Deck;
        this.p2Deck = p2Deck;
    }
}
