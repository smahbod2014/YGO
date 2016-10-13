package com.ygo.game.Types;

public enum Phase {
    DRAW_PHASE("Draw Phase"), STANDBY_PHASE("Standby Phase"), MAIN_PHASE_1("Main Phase 1"),
    BATTLE_PHASE("Battle Phase"), MAIN_PHASE_2("Main Phase 2"), END_PHASE("End Phase");

    public String description;

    Phase(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
