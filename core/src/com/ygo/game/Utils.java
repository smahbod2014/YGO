package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


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
        camera.project(screenPos, getViewportX(), getViewportY(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float scaleX = (float) YGO.GAME_WIDTH / Gdx.graphics.getWidth();
        float scaleY = (float) YGO.GAME_HEIGHT / Gdx.graphics.getHeight();
        screenPos.x *= scaleX;
        screenPos.y *= scaleY;
        return new Vector2(screenPos.x, screenPos.y);
    }

    public static <T> Array<T> convert2dArrayToGdxArray(T[][] matrix) {
        Array<T> result = new Array<T>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                result.add(matrix[i][j]);
            }
        }
        return result;
    }

    public static void prepareViewport() {
        int x = getViewportX();
        int y = getViewportY();
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        HdpiUtils.glViewport(x, y, w, h);
    }

    public static void revertViewport() {
        HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public static int getViewportX() {
        return (int) (Gdx.graphics.getWidth() * 0.104f);
    }

    public static int getViewportY() {
        return (int) (Gdx.graphics.getHeight() * 0.037f);
    }
}
