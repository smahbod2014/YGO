package com.ygo.game.buffs;

import com.ygo.game.Effect;

public abstract class Buff {

    protected Effect.Type type;

    protected Buff(Effect.Type type) {
        this.type = type;
    }

    public Effect.Type getType() {
        return type;
    }
}
