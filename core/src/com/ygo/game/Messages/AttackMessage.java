package com.ygo.game.Messages;

import com.ygo.game.Cell;
import com.ygo.game.Types.PlayerType;

public class AttackMessage {

    /** who is conducting the attack? */
    public String player;
    /** which cell is he targeting? */
    public int targetCell;
    /** which cell is he attacking from? */
    public int sourceCell;

    public AttackMessage() {

    }

    public AttackMessage(PlayerType attacker, Cell attackingCell, Cell defendingCell) {
        this.player = attacker.name();
        this.targetCell = defendingCell.index;
        this.sourceCell = attackingCell.index;
    }
}
