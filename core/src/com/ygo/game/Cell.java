package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.ygo.game.Types.Player;
import com.ygo.game.utils.Utils;

import java.util.Objects;

/**
 * Created by semahbod on 10/7/16.
 */
public class Cell {

    public static Vector2 cardSize = new Vector2();
    public Vector2 position;
    public Vector2 size;
    public Card card; //What card is in this cell?
    public Player owner;
    public int index;
    public boolean isHighlighted = false;
    public boolean targetingCursorOn;

    public Cell(float x, float z, float width, float height, Player owner) {
        this(x, z, width, height, owner, 0);
    }

    public Cell(float x, float z, float width, float height, Player owner, int index) {
        position = new Vector2(x, z);
        size = new Vector2(width, height);
        this.owner = owner;
        this.index = index;
    }

    public Vector2 getCenter() {
        return new Vector2(position.x + size.x / 2, position.y - size.y / 2);
    }

    public boolean testRay(Ray ray) {
        //1.
        Vector3 s1 = new Vector3(position.x, 0, position.y);
        Vector3 s2 = s1.cpy().add(size.x, 0, 0);
        Vector3 s3 = s1.cpy().add(0, 0, -size.y);
        Vector3 ds21 = s2.cpy().sub(s1);
        Vector3 ds31 = s3.cpy().sub(s1);
        Vector3 n = ds21.cpy().crs(ds31);

        //2.
        Vector3 dR = ray.direction;
        float ndotdR = n.dot(dR);
        if (Math.abs(ndotdR) < 1e-6f) {
            return false;
        }

        float t = -n.dot(ray.origin.cpy().sub(s1)) / ndotdR;
        Vector3 M = ray.origin.cpy().add(dR.cpy().scl(t));

        //3.
        Vector3 dMS1 = M.cpy().sub(s1);
        float u = dMS1.dot(ds21);
        float v = dMS1.dot(ds31);

        //4.
        return (u >= 0.0f && u <= ds21.dot(ds21) && v >= 0.0f && v <= ds31.dot(ds31));

    }

    public boolean hasCard() {
        return card != null;
    }

    public Vector2 getPaddedPosition2() {
        return new Vector2(position.x + (size.x - cardSize.x) / 2,
                position.y - (size.y - cardSize.y) / 2);
    }

    public Vector3 getPaddedPosition3() {
        Vector2 vec2 = getPaddedPosition2();
        return new Vector3(vec2.x, 0, vec2.y);
    }

    public void draw(ShapeRenderer sr) {
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.box(position.x, 0, position.y, size.x, 0, size.y);

        if (isHighlighted) {
            sr.set(ShapeRenderer.ShapeType.Filled);
            sr.setColor(Color.RED);
            sr.box(position.x, 0, position.y, size.x, 0, size.y);
        }
    }

    public void drawCard(DecalBatch db, Player player) {
        if (card == null) {
            return;
        }

        if (card.isBeingAnimated()) {
            float x = card.getAnimationPosition().x;
            float y = card.getAnimationPosition().y;
            float z = card.getAnimationPosition().z;
            card.drawOnField(db, x, y, z, cardSize.x, cardSize.y, player != owner);
        }
        else {
            float x = position.x + (size.x - cardSize.x) / 2;
            float z = position.y - (size.y - cardSize.y) / 2;
            card.drawOnField(db, x, z, cardSize.x, cardSize.y, player != owner);
        }
    }

    public void drawStats(SpriteBatch sb, Player player, PerspectiveCamera camera) {
        if (card == null) {
            return;
        }

        //TODO use the one in Utils
        Vector3 screenPos = new Vector3(position.x + size.x / 2, 0, position.y);
        camera.project(screenPos, Utils.getViewportX(), Utils.getViewportY(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float scaleX = (float) YGO.GAME_WIDTH / Gdx.graphics.getWidth();
        float scaleY = (float) YGO.GAME_HEIGHT / Gdx.graphics.getHeight();
        screenPos.x *= scaleX;
        screenPos.y *= scaleY;
        screenPos.y -= 5;
        YGO.cardStatsFont.draw(sb, card.getAtk() + "/" + card.getDef(), screenPos.x, screenPos.y);
    }

    @Override
    public boolean equals(Object obj) {
        Cell c = (Cell) obj;
        return index == c.index && owner == c.owner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, owner);
    }
}
