package com.ygo.game.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.ygo.game.GameStates.PlayState;

/**
 * Created by semahbod on 10/4/16.
 */
public class AttackButtonListener extends ButtonListener {

    public AttackButtonListener(PlayState playState) {
        super(playState);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        playState.chooseAttackTarget();
    }
}
