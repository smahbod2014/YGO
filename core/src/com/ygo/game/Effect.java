package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.google.common.collect.ImmutableList;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.buffs.Buff;
import com.ygo.game.buffs.StatBuff;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Effect {

    public enum Criteria {
        OnActivation, OnPlayerDrawsCard
    }

    public enum Type {
        None, ModifyAtk, ModifyDef, ModifyLevel, LifepointRecovery
    }

    public enum Flag {
        Continuous
    }

    public enum Response {
        OnAnything, OnNormalSummon
    }

    /** List of criteria that trigger this event */
    private List<Criteria> activationCriteria = new ArrayList<>();
    /** What kind of events this effect should activate in response to. Mainly used for trap cards and quick effects */
    private List<Response> responses = new ArrayList<>();
    /** This effect's class */
    private Type type = Type.None;
    /** The parameter for this effect as it corresponds to the {@link Effect#type} */
    private int value = 0;
    /** Additional information about this effect */
    private List<Flag> flags = new ArrayList<>();
    /** Function that dictates what cards this effect affects */
    private LuaFunction filter;
    /** Function that defines this card's activated ability */
    private LuaFunction operation;
    /** Unique identifier for this effect since multiple cards can have the same effect */
    private UUID id = UUID.randomUUID();

    public Effect() {
    }

    public Effect(Effect other) {
        activationCriteria = ImmutableList.copyOf(other.getActivationCriteria());
        type = other.getType();
        responses = ImmutableList.copyOf(other.getResponseCriteria());
        value = other.getValue();
        flags = ImmutableList.copyOf(other.getFlags());
        filter = other.getFilter();
        operation = other.getOperation();
    }

    public List<Criteria> getActivationCriteria() {
        return activationCriteria;
    }

    public void setActivationCriteria(Criteria c1, Criteria c2, Criteria c3, Criteria c4) {
        List<Criteria> criteria = new ArrayList<>();
        criteria.add(c1);
        criteria.add(c2);
        criteria.add(c3);
        criteria.add(c4);
        activationCriteria = criteria.stream().filter(c -> c != null).collect(Collectors.toList());
    }

    public void setResponseCriteria(Response r1, Response r2, Response r3, Response r4) {
        List<Response> responses = new ArrayList<>();
        responses.add(r1);
        responses.add(r2);
        responses.add(r3);
        responses.add(r4);
        this.responses = responses.stream().filter(c -> c != null).collect(Collectors.toList());
    }

    public List<Response> getResponseCriteria() {
        return responses;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public LuaFunction getFilter() {
        return filter;
    }

    public void setFilter(LuaFunction filter) {
        this.filter = filter;
    }

    public LuaFunction getOperation() {
        return operation;
    }

    public void setOperation(LuaFunction operation) {
        this.operation = operation;
    }

    public void setFlags(Flag f1, Flag f2, Flag f3, Flag f4) {
        List<Flag> flags = new ArrayList<>();
        flags.add(f1);
        flags.add(f2);
        flags.add(f3);
        flags.add(f4);
        this.flags = flags.stream().filter(c -> c != null).collect(Collectors.toList());
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public UUID getId() {
        return id;
    }

    public boolean satisfiesFilter(Card card) {
        if (filter == null) {
            Gdx.app.log("Effect", "Filter called in activeEffects on a card that has no filters");
        }
        return filter.call(CoerceJavaToLua.coerce(card)).toboolean();
    }

    /**
     * Called from Lua scripts
     * @return
     */
    public Effect copy() {
        return new Effect(this);
    }

    public Buff getBuff() {
        switch (type) {
            case ModifyAtk:
                return new StatBuff(value, Buff.Type.ModifyAtk);
            case ModifyDef:
                return new StatBuff(value, Buff.Type.ModifyDef);
            case ModifyLevel:
                return new StatBuff(value, Buff.Type.ModifyLevel);
            default:
                Gdx.app.log("Effect", "Unimplemented buff case");
                return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((Effect) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Effect {\n" +
                "Criteria: " + activationCriteria.stream().map(Criteria::name).collect(Collectors.joining(", ")) + "\n" +
                "Type: " + type.name() + "\n" +
                "Flags: " + flags.stream().map(Flag::name).collect(Collectors.joining(", ")) + "\n" +
                "Value: " + value + "\n" +
                "}";
    }
}