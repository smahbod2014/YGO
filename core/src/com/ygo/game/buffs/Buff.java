package com.ygo.game.buffs;

import com.ygo.game.Types.Phase;

public abstract class Buff {

    public enum Type {
        ModifyAtk, ModifyDef, ModifyLevel, NoBattleDamageTaken, NoDestructionByBattle
    }

    protected final Type type;
    /** During what phase does this buff expire? Can be null, meaning it's continuous/permanent until removed */
    protected final Phase expiration;
    /** How many times must this phase have been reached to reach the expiration point? */
    protected final int passNumber;
    protected int currentPassNumber;

    protected Buff(Type type) {
        this(type, null, 0);
    }

    protected Buff(Type type, Phase expiration, int passNumber) {
        this.type = type;
        this.expiration = expiration;
        this.passNumber = passNumber;
    }

    public Type getType() {
        return type;
    }

    public boolean isExpired() {
        return expiration != null && currentPassNumber == passNumber;
    }
}
