package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ygo.game.Types.PlayerType;

public class Hand {
    public static final int CARD_LIMIT = 7;

    private Array<Card> cards = new Array<Card>();

    private float centerX;

    public Hand(float centerX) {
        this.centerX = YGO.WINDOW_WIDTH * centerX;
    }

    public void addCard(Card card) {
        cards.add(card);
        refreshCardPositions();
    }

    private void refreshCardPositions() {

    }

    public Card getCard(int position) {
        return cards.get(position);
    }

    public void handleInput(float dt) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
        YGO.camera.project(mouse);

        for (Card card : cards) {
            if
        }
    }

    public void draw(SpriteBatch sb, PlayerType player) {
        float centerY = player == PlayerType.CURRENT_PLAYER ? Field.CURRENT_PLAYER_SPELL_TRAP_BASE.y
                : Field.OPPONENT_PLAYER_SPELL_TRAP_BASE.y;

        //"advance" is the distance between cards plus the card width
        float advance = 0;
        if (cards.size <= 5) {
            advance = Field.CARD_WIDTH_IN_CELL + Field.CARD_WIDTH_IN_CELL * 0.2f;
        }
        else {
            advance = Field.CARD_WIDTH_IN_CELL + Field.CARD_WIDTH_IN_CELL * 0.2f - Field.CARD_WIDTH_IN_CELL * 0.1f * (cards.size - 5);
        }

        float width = advance * (cards.size - 1) + Field.CARD_WIDTH_IN_CELL;
        float x = centerX - width / 2;
        for (Card card : cards) {
            card.draw(sb, x, centerY, Field.CARD_WIDTH_IN_CELL, Field.CARD_HEIGHT_IN_CELL);
            x += advance;
        }
    }
}
