package com.ygo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ygo.game.utils.Utils;

public class TargetingCursor {

    private enum State { EXPANDING, CONTRACTING }

    public static Texture cursor;

    public Sprite sprite;
    public float scalingTime = 0.75f;
    public float targetScaling = 0.75f;
    private float elapsedTime;
    private State state;

    public TargetingCursor(float x, float y) {
        init(x, y);
    }

    public TargetingCursor(Cell cell) {
        Vector2 pos = Utils.worldPerspectiveToScreen(cell.position.x + cell.size.x / 2, cell.position.y - cell.size.y / 2, Field.perspectiveCamera);
        init(pos.x, pos.y);
    }

    private void init(float x, float y) {
        state = State.CONTRACTING;
        sprite = new Sprite(cursor);
        sprite.setSize(80, 80);
        sprite.setOriginCenter();
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
    }

    private float cubicLerpFunc(float t) {
        return -2 * t * t * t + 3 * t * t;
    }

    public void update(float dt) {
        elapsedTime += dt;
        float t = elapsedTime / scalingTime;
        if (state == State.CONTRACTING) {
            sprite.setScale(MathUtils.lerp(1, targetScaling, cubicLerpFunc(t)));
        }
        else {
            sprite.setScale(MathUtils.lerp(targetScaling, 1, cubicLerpFunc(t)));
        }

        if (elapsedTime >= scalingTime) {
            elapsedTime = 0;
            if (state == State.CONTRACTING) {
                state = State.EXPANDING;
            }
            else {
                state = State.CONTRACTING;
            }
        }
    }

    public void render(SpriteBatch sb) {
        sprite.draw(sb);
    }
}
