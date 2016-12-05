package com.ygo.game.buffs;

import com.ygo.game.Effect;

/**
 * Represents a strictly numerical stat modification
 */
public class StatBuff extends Buff {

    private int boost;

    public StatBuff(int boost, Effect.Type type) {
        super(type);
        this.boost = boost;
    }

    public int getBoost() {
        return boost;
    }
}
