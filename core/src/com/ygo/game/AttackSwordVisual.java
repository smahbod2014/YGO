package com.ygo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class AttackSwordVisual {
    private enum State { RISING, FALLING }

    public static Texture sword;
    private static final float TRAVEL_OFFSET_Y = 20;

    public Sprite sprite;
    public float scalingTime = 0.75f;
    public float targetScaling = 0.75f;
    private float elapsedTime;
    private Vector2 initialPos;
    private State state;

    public AttackSwordVisual(float x, float y) {
        init(x, y);
    }

    public AttackSwordVisual(Cell cell) {
        Vector2 pos = Utils.worldPerspectiveToScreen(cell.position.x + cell.size.x / 2, cell.position.y - cell.size.y / 2, Field.perspectiveCamera);
        init(pos.x, pos.y);
    }

    private void init(float x, float y) {
        state = State.RISING;
        sprite = new Sprite(sword);
        sprite.setSize(40, 40);
        sprite.setOriginCenter();
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        initialPos = new Vector2(sprite.getX(), sprite.getY());
    }

    private float cubicLerpFunc(float t) {
        return -2 * t * t * t + 3 * t * t;
    }

    public void update(float dt) {
        elapsedTime += dt;
        float t = elapsedTime / scalingTime;
        if (state == State.RISING) {
//            sprite.setScale(MathUtils.lerp(1, targetScaling, cubicLerpFunc(t)));
            Vector2 targetPos = initialPos.cpy().add(0, TRAVEL_OFFSET_Y);
            sprite.setPosition(targetPos.x, MathUtils.lerp(initialPos.y, targetPos.y, cubicLerpFunc(t)));
        }
        else {
//            sprite.setScale(MathUtils.lerp(targetScaling, 1, cubicLerpFunc(t)));
            Vector2 targetPos = initialPos.cpy().add(0, TRAVEL_OFFSET_Y);
            sprite.setPosition(initialPos.x, MathUtils.lerp(targetPos.y, initialPos.y, cubicLerpFunc(t)));
        }

        if (elapsedTime >= scalingTime) {
            elapsedTime = 0;
            if (state == State.RISING) {
                state = State.FALLING;
            }
            else {
                state = State.RISING;
            }
        }
    }

    public void render(SpriteBatch sb) {
        sprite.draw(sb);
    }
}
