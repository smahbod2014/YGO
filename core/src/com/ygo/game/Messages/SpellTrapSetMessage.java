package com.ygo.game.Messages;

import java.util.UUID;

public class SpellTrapSetMessage {
    //who played the card?
    public int player;
    //from where?
    public int location;
    //which card?
    public UUID cardId;

    public SpellTrapSetMessage() {

    }

    public SpellTrapSetMessage(int player, int location, UUID cardId) {
        this.player = player;
        this.location = location;
        this.cardId = cardId;
    }
}
