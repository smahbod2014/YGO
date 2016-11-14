package com.ygo.game.Messages;

import com.ygo.game.Types.PlayerType;

public class AttackInitiationMessage {
    public int attackerIndex;
    public int targetIndex;
    public String attacker;

    public AttackInitiationMessage(PlayerType attacker, int attackerIndex, int targetIndex) {
        this.attackerIndex = attackerIndex;
        this.targetIndex = targetIndex;
        this.attacker = attacker.name();
    }

    public AttackInitiationMessage() {}
}
