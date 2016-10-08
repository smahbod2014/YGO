package com.ygo.game;

import com.ygo.game.Types.CardType;

import java.util.HashMap;

/**
 * Created by semahbod on 10/8/16.
 */
public class CardManager {

    private static HashMap<String, Card> cards = new HashMap<String, Card>();

    public static void add(String id, CardType cardType) {
        cards.put(id, new Card(id, cardType));
    }

    public static Card get(String id) {
        return cards.get(id);
    }

    public static Card getRandom() {
        Object[] c = cards.values().toArray();
        int index = (int) (Math.random() * c.length);
        return (Card) c[index];
    }
}
