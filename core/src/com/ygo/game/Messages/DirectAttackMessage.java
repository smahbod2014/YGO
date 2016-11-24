package com.ygo.game.Messages;

import com.ygo.game.Cell;
import com.ygo.game.Types.Player;

public class DirectAttackMessage {
    /** who is conducting the attack? */
    public String player;
    /** which cell is he attacking from? */
    public int sourceCell;

    public DirectAttackMessage() {

    }

    public DirectAttackMessage(Player attacker, Cell attackingCell) {
        this.player = attacker.name();
        this.sourceCell = attackingCell.index;
    }
}
