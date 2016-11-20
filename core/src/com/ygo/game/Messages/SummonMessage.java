package com.ygo.game.Messages;

import com.ygo.game.Types.CardPlayMode;

import java.util.UUID;

public class SummonMessage {
    //who summoned the monster?
    public int player;
    //from where?
    public int location;
    //which card?
    public UUID cardId;
    //what kind of summon?
    public int summonType;
    //in what position?
    public int cardPlayMode;

    public SummonMessage() {

    }

    public SummonMessage(int player, int location, UUID cardId, int summonType, CardPlayMode cardPlayMode) {
        this.player = player;
        this.location = location;
        this.cardId = cardId;
        this.summonType = summonType;
        this.cardPlayMode = cardPlayMode.getPlayMode();
    }
}
