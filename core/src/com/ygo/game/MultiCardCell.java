package com.ygo.game;

import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ygo.game.Types.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by semahbod on 10/8/16.
 */
public class MultiCardCell extends Cell {

    public List<Card> cards = new ArrayList<>();

    public MultiCardCell(float x, float z, float width, float height, Player owner) {
        super(x, z, width, height, owner);
    }

    @Override
    public Vector3 getPaddedPosition3() {
        Vector2 vec2 = getPaddedPosition2();
        return new Vector3(vec2.x, Card.THICKNESS * (cards.size()), vec2.y);
    }

    @Override
    public void drawCard(DecalBatch db, Player player) {
        float x = position.x + (size.x - cardSize.x) / 2;
        float z = position.y - (size.y - cardSize.y) / 2;

        float y = 0;
        for (Card card : cards) {
            if (card.isBeingAnimated()) {
                card.drawOnField(db, card.getAnimationPosition().x, card.getAnimationPosition().y,
                        card.getAnimationPosition().z,
                        cardSize.x, cardSize.y, player != owner);
            }
            else {
                card.drawOnField(db, x, y, z, cardSize.x, cardSize.y, player != owner);
                y += Card.THICKNESS;
            }
        }
    }
}
