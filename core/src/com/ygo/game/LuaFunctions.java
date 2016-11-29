package com.ygo.game;

import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Types.DamageType;
import com.ygo.game.Types.Player;
import com.ygo.game.utils.TriConsumer;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.util.Set;
import java.util.function.BiConsumer;

public class LuaFunctions {

    private LuaFunctions() {}

    @Deprecated
    public static void registerFunctions(PlayState playState, Set<Card> cardsInPlay) {
        Globals globals = CardManager.getGlobals();
        globals.set("inflictDamage", new InflictDamage(playState::inflictDamage));
        globals.set("increaseLifepoints", new IncreaseLifepoints(playState::increaseLifepoints));
//        cardsInPlay.forEach(card -> globals.load(Gdx.files.internal("scripts/" + card.getSerial() + ".lua").readString()).call());
//        globals.load(Gdx.files.internal("scripts/c84257640.lua").readString()).call();
    }

    public static class TestFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            System.out.println(arg.toint());
            return NIL;
        }
    }

    public static class InflictDamage extends ThreeArgFunction {
        TriConsumer<Player, DamageType, Integer> function;

        public InflictDamage(TriConsumer<Player, DamageType, Integer> function) {
            this.function = function;
        }

        @Override
        public LuaValue call(LuaValue target, LuaValue damageType, LuaValue amount) {
            function.accept(Player.valueOf(target.tojstring()), DamageType.valueOf(damageType.tojstring()), amount.toint());
            return NIL;
        }
    }

    public static class IncreaseLifepoints extends TwoArgFunction {
        BiConsumer<Player, Integer> function;

        public IncreaseLifepoints(BiConsumer<Player, Integer> function) {
            this.function = function;
        }

        @Override
        public LuaValue call(LuaValue target, LuaValue amount) {
            function.accept(Player.valueOf(target.tojstring()), amount.toint());
            return NIL;
        }
    }
}
