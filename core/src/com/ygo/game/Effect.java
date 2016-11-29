package com.ygo.game;

import com.google.common.collect.ImmutableList;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Effect {

    public enum ActivationCriteria {
        OnActivation("CRITERIA_ON_ACTIVATION", 0),
        OnPlayerDrawsCard("CRITERIA_PLAYER_DRAWS_CARD", 1);

        String luaName;
        int value;

        ActivationCriteria(String luaName, int value) {
            this.luaName = luaName;
            this.value = value;
        }

        public String getLuaName() {
            return luaName;
        }

        public int getValue() {
            return value;
        }

        public static ActivationCriteria fromValue(int value) {
            return Arrays.stream(values()).filter(a -> a.getValue() == value).findFirst().get();
        }
    }

    public enum Type {
        ModifyATK("MODIFY_ATK", 0),
        ModifyDEF("MODIFY_DEF", 1);

        String luaName;
        int value;

        Type(String luaName, int value) {
            this.luaName = luaName;
            this.value = value;
        }

        public String getLuaName() {
            return luaName;
        }

        public int getValue() {
            return value;
        }

        public static Type fromValue(int value) {
            return Arrays.stream(values()).filter(a -> a.getValue() == value).findFirst().get();
        }
    }

    /**
     * List of criteria that trigger this event
     */
    private List<ActivationCriteria> activationCriteria = new ArrayList<>();
    /**
     * This effect's class
     */
    private Type type;
    /**
     * The parameter for this effect as it corresponds to the {@link Effect#type}
     */
    private int value;
    /**
     * Function that dictates what cards this effect affects
     */
    private LuaFunction filter;

    public Effect() {
    }

    public List<ActivationCriteria> getActivationCriteria() {
        return activationCriteria;
    }

    public void setActivationCriteria(Integer c1, Integer c2, Integer c3, Integer c4) {
        List<Integer> args = ImmutableList.of(c1, c2, c3, c4);
        for (Integer i : args) {
            if (i == null) {
                break;
            }
            activationCriteria.add(ActivationCriteria.fromValue(i));
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(int type) {
        this.type = Type.fromValue(type);
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

    @Override
    public String toString() {
        return "Effect {\n" +
                "Criteria: " + activationCriteria.stream().map(ActivationCriteria::name).collect(Collectors.joining(", ")) + "\n" +
                "Type: " + type.name() + "\n" +
                "Value: " + value + "\n" +
                "}";
    }

    public static class CreateEffect extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return CoerceJavaToLua.coerce(new Effect());
        }
    }
}