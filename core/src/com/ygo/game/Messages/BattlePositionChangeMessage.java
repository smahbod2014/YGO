package com.ygo.game.Messages;

import com.ygo.game.Card;
import com.ygo.game.Types.CardPlayMode;

/**
 * Created by semahbod on 11/21/16.
 */
public class BattlePositionChangeMessage {
    public String cardId;
    public int battlePosition;

    public BattlePositionChangeMessage(Card card, CardPlayMode battlePosition) {
        this.cardId = card.getUniqueId().toString();
        this.battlePosition = battlePosition.getPlayMode();
    }

    public BattlePositionChangeMessage() {}
}
