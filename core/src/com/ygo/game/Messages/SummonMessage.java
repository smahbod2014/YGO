package com.ygo.game.Messages;

import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.Player;
import com.ygo.game.Types.SummonType;

import java.util.UUID;

public class SummonMessage {
    //who summoned the monster?
    public String player;
    //from where?
    public String location;
    //which card?
    public UUID cardId;
    //what kind of summon?
    public String summonType;
    //in what position?
    public int cardPlayMode;

    public SummonMessage() {

    }

    public SummonMessage(Player player, Location location, UUID cardId, SummonType summonType, CardPlayMode cardPlayMode) {
        this.player = player.name();
        this.location = location.name();
        this.cardId = cardId;
        this.summonType = summonType.name();
        this.cardPlayMode = cardPlayMode.getPlayMode();
    }
}
