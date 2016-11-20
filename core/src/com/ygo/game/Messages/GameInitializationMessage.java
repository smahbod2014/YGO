package com.ygo.game.Messages;

import com.ygo.game.Pair;

import java.util.List;
import java.util.UUID;

public class GameInitializationMessage {
    //unique card id to card serial number or name
    public List<Pair<UUID, String>> p1Deck, p2Deck;

    public GameInitializationMessage() {

    }

    public GameInitializationMessage(List<Pair<UUID, String>> p1Deck, List<Pair<UUID, String>> p2Deck) {
        this.p1Deck = p1Deck;
        this.p2Deck = p2Deck;
    }
}
