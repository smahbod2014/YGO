package com.ygo.game.Tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.TimeUtils;
import com.ygo.game.Cell;
import com.ygo.game.Field;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.GameStates.StateManager;
import com.ygo.game.MultiCardCell;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.Player;
import com.ygo.game.Types.Zone;
import com.ygo.game.YGO;

import java.util.Random;

/**
 * Created by semahbod on 10/8/16.
 */
@Deprecated
public class Tests {

    private static Random random = new Random();

    public static void input(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            long start = TimeUtils.millis();
            testCardFitInCells(((PlayState) StateManager.getCurrentState()).field);
            double elapsed = TimeUtils.timeSinceMillis(start) / 1000.0;
            YGO.info("Generated cards in " + elapsed + " seconds");
        }
    }

    private static void testCardFitInCells(Field field) {
        for (Player p : Player.values()) {
            for (Zone z : Zone.values()) {
                for (Cell c : field.getZone(z, p)) {
                    switch (z) {
                        case DECK:
                            fillUpDeck(c, 60, CardPlayMode.FACE_DOWN);
                            break;
                        case EXTRA_DECK:
                            fillUpDeck(c, 15, CardPlayMode.FACE_DOWN);
                            break;
                        case GRAVEYARD:
                            fillUpDeck(c, 20, CardPlayMode.FACE_UP);
                            break;
                        case BANISHED:
                            fillUpDeck(c, 10, CardPlayMode.FACE_UP);
                            break;
                        default:
                            int playMode = CardPlayMode.FACE_UP;
                            if (z == Zone.MONSTER && random.nextBoolean()) {
                                playMode = CardPlayMode.FACE_DOWN | CardPlayMode.DEFENSE_MODE;
                            }
                            if (z == Zone.SPELL_TRAP && random.nextBoolean()) {
                                playMode = CardPlayMode.FACE_DOWN;
                            }
//                            field.placeCardOnField(new Card(), z, p, playMode, Location.FIELD);
                            break;
                    }
                }
            }
        }
    }

    private static void fillUpDeck(Cell c, int numCards, int mode) {
        MultiCardCell multiCardCell = (MultiCardCell) c;
        for (int i = 0; i < numCards; i++) {
//            Card card = CardManager.getRandom().copy();
//            card.playMode = mode;
//            multiCardCell.cards.add(card);
        }
    }
}
