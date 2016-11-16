package com.ygo.game;

import com.ygo.game.Types.CardType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by semahbod on 10/8/16.
 */
public class CardManager {

    private static Map<String, Card> cards = new HashMap<String, Card>();
    private static Set<Integer> chosenCards = new HashSet<Integer>();
    private static Map<UUID, Card> cardsInPlay = new HashMap<>();

    public static void add(String id, int cardType) {
        cards.put(id, new Card(id, cardType));
    }

    public static void add(String id, int cardType, int atk, int def, int level) {
        cards.put(id, new Card(id, cardType, atk, def, level));
    }

    public static Card get(String id) {
        return cards.get(id);
    }
    public static Card getUnique(UUID id) {
        return cardsInPlay.get(id);
    }

    public static void submitCardForPlay(Card card) {
        cardsInPlay.put(card.uniqueId, card);
    }

    public static Card getRandom() {
        Object[] c = cards.values().toArray();
        int index = (int) (Math.random() * c.length);
        return (Card) c[index];
    }

    public static Card getRandomNoDuplicates() {
        Object[] c = cards.values().toArray();
        if (c.length == chosenCards.size())
            throw new RuntimeException("No more cards to choose from");
        int index;
        do {
            index = (int) (Math.random() * c.length);
        } while (chosenCards.contains(index));
        chosenCards.add(index);
        return (Card) c[index];
    }

    public static void clearDuplicatesHistory() {
        chosenCards.clear();
    }
}
