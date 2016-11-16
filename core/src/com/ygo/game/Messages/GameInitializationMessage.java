package com.ygo.game.Messages;

import com.ygo.game.Card;
import com.ygo.game.Pair;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameInitializationMessage {
    //unique card id to card serial number
    public List<Pair<UUID, String>> p1Deck, p2Deck;

    public GameInitializationMessage() {

    }

    public GameInitializationMessage(List<Card> p1Deck, List<Card> p2Deck) {
        this.p1Deck = p1Deck.stream().map(card -> new Pair<>(card.uniqueId, card.id)).collect(Collectors.toList());
        this.p2Deck = p2Deck.stream().map(card -> new Pair<>(card.uniqueId, card.id)).collect(Collectors.toList());
    }
}
