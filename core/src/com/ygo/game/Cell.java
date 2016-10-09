package com.ygo.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ygo.game.Types.PlayerType;

/**
 * Created by semahbod on 10/7/16.
 */
public class Cell {

    public static Vector2 cardSize = new Vector2();
    public Vector2 position;
    public Vector2 size;
    public Card card; //What card is in this cell?
    public PlayerType owner;

    public Cell(float x, float z, float width, float height, PlayerType owner) {
        position = new Vector2(x, z);
        size = new Vector2(width, height);
        this.owner = owner;
    }

    public void draw(ShapeRenderer sr) {
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.box(position.x, 0, position.y, size.x, 0, size.y);
    }

    public void drawCard(DecalBatch db, PlayerType player) {
        if (card == null) {
            return;
        }

        float x = position.x + (size.x - cardSize.x) / 2;
        float z = position.y - (size.y - cardSize.y) / 2;
        card.drawOnField(db, x, z, cardSize.x, cardSize.y, player != owner);
    }
}
