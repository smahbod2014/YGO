package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by semahbod on 9/30/16.
 */
public class Field {

    private final int CELL_SIZE = YGO.WINDOW_WIDTH / 10;
    private final int MIDDLE_DIVIDE = CELL_SIZE / 2;
    private final int CELLS_IN_ROW = 5;

    private ShapeRenderer sr;
    private float xCoord, yCoord;

    public Field() {
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(YGO.camera.combined);
        sr.setColor(Color.WHITE);
        Gdx.app.debug("YGO", "CELL_SIZE = " + CELL_SIZE);

        xCoord = YGO.WINDOW_WIDTH * 7 / 24 - CELLS_IN_ROW * CELL_SIZE / 2;
        yCoord = YGO.WINDOW_HEIGHT / 2 - (CELL_SIZE * 4 + MIDDLE_DIVIDE) / 2;
    }

    public void renderGrid() {
//        float x = YGO.WINDOW_WIDTH / 2 - CELL_SIZE / 2;
//        float y = YGO.WINDOW_HEIGHT / 2 - CELL_SIZE / 2;
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.RED);
        sr.line(YGO.WINDOW_WIDTH/2, YGO.WINDOW_HEIGHT, YGO.WINDOW_WIDTH/2, 0);
        sr.setColor(Color.WHITE);
        for (int i = 0; i < CELLS_IN_ROW; i++) {
            float y = yCoord;
            sr.rect(xCoord + CELL_SIZE * i, y, CELL_SIZE, CELL_SIZE);
            y += CELL_SIZE;
            sr.rect(xCoord + CELL_SIZE * i, y, CELL_SIZE, CELL_SIZE);
            y += MIDDLE_DIVIDE + CELL_SIZE;
            sr.rect(xCoord + CELL_SIZE * i, y, CELL_SIZE, CELL_SIZE);
            y += CELL_SIZE;
            sr.rect(xCoord + CELL_SIZE * i, y, CELL_SIZE, CELL_SIZE);
        }
        sr.end();
    }
}
