package com.ygo.game.Messages;

import com.ygo.game.Types.Phase;

public class PhaseChangeMessage {
    public String newPhase;

    public PhaseChangeMessage(Phase newPhase) {
        this.newPhase = newPhase.name();
    }

    public PhaseChangeMessage() {
    }
}
