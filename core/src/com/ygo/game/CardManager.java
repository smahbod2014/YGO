package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygo.game.GameStates.PlayState;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

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
        globals.set("inflictDamage", new LuaFunctions.InflictDamage(playState::inflictDamage));
        globals.set("increaseLifepoints", new LuaFunctions.IncreaseLifepoints(playState::increaseLifepoints));
//        cardsInPlay.forEach(card -> globals.load(Gdx.files.internal("scripts/" + card.getSerial() + ".lua").readString()).call());
//        globals.load(Gdx.files.internal("scripts/c84257640.lua").readString()).call();
        cardsInPlay.forEach(card -> {
            FileHandle script = Gdx.files.internal("scripts/c" + card.getSerial() + ".lua");
            if (script.exists()) {
                String table = "c" + card.getSerial() + " = {}\n";
                globals.load(table + script.readString()).call();
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
