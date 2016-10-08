package com.ygo.game;

import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Array;
import com.ygo.game.Types.PlayerType;

/**
 * Created by semahbod on 10/8/16.
 */
public class MultiCardCell extends Cell {

    public Array<Card> cards = new Array<Card>();

    public MultiCardCell(float x, float z, float width, float height) {
        super(x, z, width, height);
    }

    @Override
    public void drawCard(DecalBatch db, PlayerType player) {
        float x = position.x + (size.x - cardSize.x) / 2;
        float z = position.y - (size.y - cardSize.y) / 2;

        float y = 0;
        for (Card card : cards) {
            card.drawOnField(db, x, y, z, cardSize.x, cardSize.y, player);
            y += Card.THICKNESS;
        }
    }
}
