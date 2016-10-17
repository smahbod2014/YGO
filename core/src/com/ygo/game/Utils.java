package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class Utils {

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

    public static Vector2 getMousePos(Camera camera) {
        Vector3 m = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(m);
        return new Vector2(m.x, m.y);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T> void reverseArray(T[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            T temp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = temp;
        }
    }

    public static Vector2 worldPerspectiveToScreen(float x, float z, Camera camera) {
        Vector3 screenPos = new Vector3(x, 0, z);
        camera.project(screenPos, Field.getViewportX(), Field.getViewportY(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float scaleX = (float) YGO.GAME_WIDTH / Gdx.graphics.getWidth();
        float scaleY = (float) YGO.GAME_HEIGHT / Gdx.graphics.getHeight();
        screenPos.x *= scaleX;
        screenPos.y *= scaleY;
        return new Vector2(screenPos.x, screenPos.y);
    }
}
