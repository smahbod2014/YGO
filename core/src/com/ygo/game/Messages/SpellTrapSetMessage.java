package com.ygo.game.Messages;

public class SpellTrapSetMessage {
    //who played the card?
    public int player;
    //from where?
    public int location;
    //which card?
    public String cardId;

    public SpellTrapSetMessage() {

    }

    public SpellTrapSetMessage(int player, int location, String cardId) {
        this.player = player;
        this.location = location;
        this.cardId = cardId;
    }
}
