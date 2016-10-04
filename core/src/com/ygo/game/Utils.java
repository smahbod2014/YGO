package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Utils {

    public static final int TARGET_WIDTH = 1280;
    public static final int TARGET_HEIGHT = 720;

    /**
     * Converts an absolute horizontal pixel value to a scaled pixel value
     * @param x
     * @return
     */
    public static float sx(float x) {
        return x / 1280.0f * YGO.WINDOW_WIDTH;
    }

    /**
     * Converts an absolute vertical pixel value to a scaled value
     * @param y
     * @return
     */
    public static float sy(float y) {
        return y / 720.0f * YGO.WINDOW_HEIGHT;
    }

    public static float getCurrentWindowScaleX() {
        return (float) Gdx.graphics.getWidth() / TARGET_WIDTH;
    }

    public static float getCurrentWindowScaleY() {
        return (float) Gdx.graphics.getHeight() / TARGET_HEIGHT;
    }

    public static float width() {
        return Gdx.graphics.getWidth();
    }

    public static float height() {
        return Gdx.graphics.getHeight();
    }

    public static Vector2 getMousePos() {
        Vector3 m = new Vector3(Gdx.input.getX(), height() - Gdx.input.getY(), 0);
        YGO.camera.project(m);
        return new Vector2(m.x, m.y);
    }
}
