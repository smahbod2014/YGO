package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Types.Race;
import com.ygo.game.Types.Zone;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by semahbod on 10/8/16.
 */
public class CardManager {

    @Deprecated
    private static Set<Integer> chosenCards = new HashSet<>();
    private static Map<UUID, Card> cardsInPlay = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static final Globals globals = JsePlatform.standardGlobals();

    public static Globals getGlobals() {
        return globals;
    }

    public static Texture getOrLoadTexture(String serial) {
        if (!textures.containsKey(serial)) {
            textures.put(serial, new Texture(Gdx.files.internal("cards/" + serial + ".jpg")));
        }
        return textures.get(serial);
    }

    public static Card getUnique(UUID id) {
        return cardsInPlay.get(id);
    }

    public static Card getUnique(String id) {
        return getUnique(UUID.fromString(id));
    }

    public static Set<Card> getUniqueCardsInPlay() {
        return cardsInPlay.values().stream().distinct().collect(Collectors.toSet());
    }

    @Deprecated
    public static void submitCardForPlay(Card card) {
        cardsInPlay.put(card.getUniqueId(), card);
    }

    public static void submitCardsForPlay(List<Card> cards) {
        cardsInPlay.putAll(cards.stream().collect(Collectors.toMap(Card::getUniqueId, Function.identity())));
    }

    public static void initializeLuaScripts(PlayState playState, Set<Card> cardsInPlay) {
        Globals globals = CardManager.getGlobals();
        globals.set("Duel", LuaValue.tableOf());
        globals.get("Duel").set("testFunction", new LuaFunctions.TestFunction());
        globals.get("Duel").set("inflictDamage", new LuaFunctions.InflictDamage(playState::inflictDamage));
        globals.get("Duel").set("increaseLifepoints", new LuaFunctions.IncreaseLifepoints(playState::increaseLifepoints));
        globals.set(Zone.class.getSimpleName(), LuaValue.tableOf());
        globals.set(Race.class.getSimpleName(), LuaValue.tableOf());
        globals.set(Effect.Criteria.class.getSimpleName(), LuaValue.tableOf());
        globals.set(Effect.Type.class.getSimpleName(), LuaValue.tableOf());
        globals.set(Effect.Flag.class.getSimpleName(), LuaValue.tableOf());
        Arrays.stream(Zone.values()).forEach(z -> globals.get(z.getClass().getSimpleName()).set(z.name(), CoerceJavaToLua.coerce(z)));
        Arrays.stream(Race.values()).forEach(r -> globals.get(r.getClass().getSimpleName()).set(r.name(), CoerceJavaToLua.coerce(r)));
        Arrays.stream(Effect.Criteria.values()).forEach(c -> globals.get(c.getClass().getSimpleName()).set(c.name(), CoerceJavaToLua.coerce(c)));
        Arrays.stream(Effect.Type.values()).forEach(t -> globals.get(t.getClass().getSimpleName()).set(t.name(), CoerceJavaToLua.coerce(t)));
        Arrays.stream(Effect.Flag.values()).forEach(f -> globals.get(f.getClass().getSimpleName()).set(f.name(), CoerceJavaToLua.coerce(f)));
        cardsInPlay.forEach(card -> {
            FileHandle script = Gdx.files.internal("scripts/c" + card.getSerial() + ".lua");
            if (script.exists()) {
                String cardTable = String.format("c%s", card.getSerial());
                globals.set(cardTable, LuaValue.tableOf());
                globals.load(script.readString()).call();
                //TODO: Have to set up listeners here for different card activation criteria
                globals.get(cardTable).get("initialize").call(CoerceJavaToLua.coerce(card));
            }
            else {
                Gdx.app.log("CardManager", "No script found for " + card.getName());
            }
        });
    }

//    public static Card getRandom() {
//        Object[] c = cards.values().toArray();
//        int index = (int) (Math.random() * c.length);
//        return (Card) c[index];
//    }
//
//    public static Card getRandomNoDuplicates() {
//        Object[] c = cards.values().toArray();
//        if (c.length == chosenCards.size())
//            throw new RuntimeException("No more cards to choose from");
//        int index;
//        do {
//            index = (int) (Math.random() * c.length);
//        } while (chosenCards.contains(index));
//        chosenCards.add(index);
//        return (Card) c[index];
//    }

//    public static void clearDuplicatesHistory() {
//        chosenCards.clear();
//    }
}
