package com.ygo.game.Messages;

import com.ygo.game.Types.PlayerType;

public class DirectAttackInitiationMessage {
    public int attackerIndex;
    public String attacker;

    public DirectAttackInitiationMessage(PlayerType attacker, int attackerIndex) {
        this.attackerIndex = attackerIndex;
        this.attacker = attacker.name();
    }

    public DirectAttackInitiationMessage() {}
}
