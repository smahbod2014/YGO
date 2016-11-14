package com.ygo.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Lifepoints {

    public Vector2 position;
    public Vector2 size;
    public int startingLifepoints;
    public int currentLifePoints;
    public String playerName;

    public Lifepoints(float x, float y, float width, float height, int startingLifepoints, String playerName) {
        position = new Vector2(x, y);
        size = new Vector2(width, height);
        this.currentLifePoints = this.startingLifepoints = startingLifepoints;
        this.playerName = playerName;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        float amountFilled = size.x * currentLifePoints / startingLifepoints;
        //TODO: Do not call begin() and end() here
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(190f / 255, 25f / 255, 0, 1); // dark red
        shapeRenderer.rect(position.x, position.y, amountFilled, size.y);
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(position.x, position.y, size.x, size.y);
        Vector2 fontDimensions = Utils.getFontDimensions(YGO.cardStatsFont, Integer.toString(currentLifePoints));
        shapeRenderer.end();
        Vector2 fontPosition = position.cpy().add(size.x / 2 - fontDimensions.x / 2, size.y / 2 + fontDimensions.y / 2);
        batch.begin();
        YGO.cardStatsFont.draw(batch, Integer.toString(currentLifePoints), fontPosition.x, fontPosition.y);
        YGO.cardStatsFont.draw(batch, playerName, position.x, position.y - 5);
        batch.end();
    }
}
