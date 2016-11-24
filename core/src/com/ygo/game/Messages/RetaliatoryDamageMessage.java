package com.ygo.game.Messages;

import com.ygo.game.Cell;
import com.ygo.game.Types.Player;

public class RetaliatoryDamageMessage {
    public String victimPlayer;
    public int attackingCellIndex;
    public int defendingCellIndex;
    public int damage;

    public RetaliatoryDamageMessage(Player victim, Cell attackingCell, Cell defendingCell, int damage) {
        this.victimPlayer = victim.name();
        this.attackingCellIndex = attackingCell.index;
        this.defendingCellIndex = defendingCell.index;
        this.damage = damage;
    }

    public RetaliatoryDamageMessage() {}
}
