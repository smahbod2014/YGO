package com.ygo.game.Messages;

public class SummonMessage {
    //who summoned the monster?
    public int player;
    //from where?
    public int location;
    //which card?
    public String cardId;
    //what kind of summon?
    public int summonType;
    //in what position?
    public int cardPlayMode;

    public SummonMessage() {

    }

    public SummonMessage(int player, int location, String cardId, int summonType, int cardPlayMode) {
        this.player = player;
        this.location = location;
        this.cardId = cardId;
        this.summonType = summonType;
        this.cardPlayMode = cardPlayMode;
    }
}
