package com.ygo.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by semahbod on 10/7/16.
 */
public class Cell {

    public Vector2 position;
    public Vector2 size;
    public Card card; //What card is in this cell?

    public Cell(float x, float z, float width, float height) {
        position = new Vector2(x, z);
        size = new Vector2(width, height);
    }

    public void draw(ShapeRenderer sr) {
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.box(position.x, 0, position.y, size.x, 0, size.y);
    }
}
