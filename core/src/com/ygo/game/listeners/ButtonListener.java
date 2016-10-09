package com.ygo.game.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ygo.game.GameStates.PlayState;

/**
 * Created by semahbod on 10/8/16.
 */
public abstract class ButtonListener extends ClickListener {

    protected PlayState playState;

    protected ButtonListener(PlayState playState) {
        this.playState = playState;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        playState.hideCardMenu();
    }
}
