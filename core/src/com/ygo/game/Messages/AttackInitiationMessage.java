package com.ygo.game.Messages;

import com.ygo.game.Types.Player;

public class AttackInitiationMessage {
    public int attackerIndex;
    public int targetIndex;
    public String attacker;

    public AttackInitiationMessage(Player attacker, int attackerIndex, int targetIndex) {
        this.attackerIndex = attackerIndex;
        this.targetIndex = targetIndex;
        this.attacker = attacker.name();
    }

    public AttackInitiationMessage() {}
}
