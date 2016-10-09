package com.ygo.game.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ygo.game.Field;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.YGO;

/**
 * Created by semahbod on 10/4/16.
 */
public class NormalSummonButtonListener extends ButtonListener {

    public NormalSummonButtonListener(PlayState playState) {
        super(playState);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        playState.performNormalSummon();
    }
}
