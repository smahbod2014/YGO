package com.ygo.game.Tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygo.game.CardManager;
import com.ygo.game.Cell;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.ZoneType;
import com.ygo.game.YGO;

/**
 * Created by semahbod on 10/8/16.
 */
public class Tests {

    public static void input(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            testCardFitInCells();
        }
    }

    private static void testCardFitInCells() {
        for (PlayerType p : PlayerType.values()) {
            for (ZoneType z : ZoneType.values()) {
                for (Cell c : YGO.field.getZone(z, p)) {
                    YGO.field.placeCardOnField(CardManager.getRandom().copy(), z, p, CardPlayMode.FACE_UP | CardPlayMode.ATTACK_MODE);
                }
            }
        }
    }
}
