package com.ygo.game.buffs;

/**
 * Represents a strictly numerical stat modification
 */
public class StatBuff extends Buff {

    private int boost;

    public StatBuff(int boost, Type type) {
        super(type);
        this.boost = boost;
    }

    public int getBoost() {
        return boost;
    }
}
