package com.ygo.game.buffs;

/**
 * Represents a buff that describes something about the nature of this card,
 * e.g. cannot be destroyed by battle, deals piercing damage, unaffected by X...
 */
public class PropertyBuff extends Buff {

    public PropertyBuff(Type type) {
        super(type);
    }
}
