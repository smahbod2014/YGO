package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Types.DamageType;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.utils.TriConsumer;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.Set;
import java.util.function.BiConsumer;

public class LuaFunctions {

    private LuaFunctions() {}

    public static void registerFunctions(PlayState playState, Set<Card> cardsInPlay) {
        Globals globals = CardManager.getGlobals();
        globals.set("inflictDamage", new InflictDamage(playState::inflictDamage));
        globals.set("increaseLifepoints", new IncreaseLifepoints(playState::increaseLifepoints));
//        cardsInPlay.forEach(card -> globals.load(Gdx.files.internal("scripts/" + card.getSerial() + ".lua").readString()).call());
//        globals.load(Gdx.files.internal("scripts/c84257640.lua").readString()).call();
    }

    public static class InflictDamage extends ThreeArgFunction {
        TriConsumer<PlayerType, DamageType, Integer> function;

        public InflictDamage(TriConsumer<PlayerType, DamageType, Integer> function) {
            this.function = function;
        }

        @Override
        public LuaValue call(LuaValue target, LuaValue damageType, LuaValue amount) {
            function.accept(PlayerType.valueOf(target.tojstring()), DamageType.valueOf(damageType.tojstring()), amount.toint());
            return NIL;
        }
    }

    public static class IncreaseLifepoints extends TwoArgFunction {
        BiConsumer<PlayerType, Integer> function;

        public IncreaseLifepoints(BiConsumer<PlayerType, Integer> function) {
            this.function = function;
        }

        @Override
        public LuaValue call(LuaValue target, LuaValue amount) {
            function.accept(PlayerType.valueOf(target.tojstring()), amount.toint());
            return NIL;
        }
    }
}
