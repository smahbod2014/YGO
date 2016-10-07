package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Utils {

    public static final int TARGET_WIDTH = 1;
    public static final int TARGET_HEIGHT = 1;

    public static float getCurrentWindowScaleX() {
        return width() / TARGET_WIDTH;
    }

    public static float getCurrentWindowScaleY() {
        return height() / TARGET_HEIGHT;
    }

    public static float width() {
        return Gdx.graphics.getWidth();
    }

    public static float height() {
        return Gdx.graphics.getHeight();
    }

    public static float convertX(float x) {
        return x * YGO.GAME_WIDTH / Gdx.graphics.getWidth();
    }

    public static float convertY(float y) {
        return y * YGO.GAME_HEIGHT / Gdx.graphics.getHeight();
    }

    public static Vector2 getMousePos() {
        Vector3 m = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        YGO.camera.unproject(m);
        return new Vector2(m.x, m.y);
    }
}
