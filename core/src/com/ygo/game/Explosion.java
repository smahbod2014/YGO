package com.ygo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ygo.game.utils.Utils;

public class Explosion {

    public static Texture spritesheet;

    private Animation animation;
    private float frameDuration = 0.125f;
    private float stateTime = 0;
    public boolean isDead;
    public float x, y, width, height;

    public Explosion(float x, float y) {
        init(x, y);
    }

    public Explosion(Cell cell) {
        Vector2 pos = Utils.worldPerspectiveToScreen(cell.position.x + cell.size.x / 2, cell.position.y - cell.size.y / 2, Field.perspectiveCamera);
        init(pos.x, pos.y);
    }

    private void init(float x, float y) {
        TextureRegion[][] frames = TextureRegion.split(spritesheet, spritesheet.getWidth() / 4, spritesheet.getHeight() / 4);
        animation = new Animation(frameDuration, Utils.convert2dArrayToGdxArray(frames));
        width = 100;
        height = 100;
        this.x = x - width / 2;
        this.y = y - height / 2;
    }

    public void update(float dt) {
        stateTime += dt;
        if (animation.isAnimationFinished(stateTime)) {
            isDead = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(stateTime), x, y, width, height);
    }
}
