package com.ygo.game;

import com.google.common.collect.ImmutableList;
import com.ygo.game.GameStates.PlayState;

import org.luaj.vm2.LuaFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Effect {

    public enum Criteria {
        OnActivation, OnPlayerDrawsCard
    }

    public enum Type {
        ModifyAtk, ModifyDef
    }

    public enum Flag {
        Continuous
    }

    /**
     * List of criteria that trigger this event
     */
    private List<Criteria> activationCriteria = new ArrayList<>();
    /**
     * This effect's class(es)
     */
    private List<Type> types = new ArrayList<>();
    /**
     * The parameter for this effect as it corresponds to the {@link Effect#types}
     */
    private int value;
    /**
     * Additional information about this effect
     */
    private List<Flag> flags = new ArrayList<>();
    /**
     * Function that dictates what cards this effect affects
     */
    private LuaFunction filter;

    public Effect() {
    }

    public Effect(Effect other) {
        activationCriteria = ImmutableList.copyOf(other.getActivationCriteria());
        types = ImmutableList.copyOf(other.getTypes());
        value = other.getValue();
        flags = ImmutableList.copyOf(other.getFlags());
        filter = other.getFilter();
    }

    public List<Criteria> getActivationCriteria() {
        return activationCriteria;
    }

    public void setActivationCriteria(Criteria c1, Criteria c2, Criteria c3, Criteria c4) {
        activationCriteria = ImmutableList.of(c1, c2, c3, c4).stream().filter(c -> c != null).collect(Collectors.toList());
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(Type t1, Type t2, Type t3, Type t4) {
        types = ImmutableList.of(t1, t2, t3, t4).stream().filter(t -> t != null).collect(Collectors.toList());
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

    public void setFlags(Flag f1, Flag f2, Flag f3, Flag f4) {
        flags = ImmutableList.of(f1, f2, f3, f4).stream().filter(f -> f != null).collect(Collectors.toList());
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public Effect copy() {
        return new Effect(this);
    }

    public void handle(PlayState playState, Set<Card> uniqueCardsInPlay) {
        types.forEach(type -> {
            switch (type) {
                case ModifyAtk:
            }
        });
    }

    @Override
    public String toString() {
        return "Effect {\n" +
                "Criteria: " + activationCriteria.stream().map(Criteria::name).collect(Collectors.joining(", ")) + "\n" +
                "Types: " + types.stream().map(Type::name).collect(Collectors.joining(", ")) + "\n" +
                "Flags: " + flags.stream().map(Flag::name).collect(Collectors.joining(", ")) + "\n" +
                "Value: " + value + "\n" +
                "}";
    }
}