package com.ygo.game.Messages;

import com.ygo.game.Types.PlayerType;

public class AttackMessage {

    /** who is conducting the attack? */
    public String player;
    /** which cell is he targeting? */
    public int targetCell;
    /** what card is he attacking with? */
    public String cardId;

    public AttackMessage() {

    }

    public AttackMessage(PlayerType player, int targetCell, String cardId) {
        this.player = player.name();
        this.targetCell = targetCell;
        this.cardId = cardId;
    }
}
