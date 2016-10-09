package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;

public class Hand {
    public static final int CARD_LIMIT = 7;
    public static final float CARD_GAP = 15;

    private Array<Card> cards = new Array<Card>();

    private float centerX;
    private PlayerType player;
    PlayState playState;

    public Hand(PlayState state, float centerX, PlayerType player) {
        this.centerX = YGO.GAME_WIDTH * centerX;
        this.player = player;
        playState = state;
    }

    public void addCard(Card card) {
        cards.add(card);
        card.setLocation(Location.HAND);
        refreshCardPositions();
    }

    public void removeCard(Card card) {
        cards.removeValue(card, true);
        refreshCardPositions();
    }

    private void refreshCardPositions() {
        //"advance" is the distance between cards plus the card width
        float advance = 0;
        if (cards.size <= 5) {
            advance = Card.SIZE_IN_HAND_NEAR.x + CARD_GAP;
        }
        else {
            advance = Card.SIZE_IN_HAND_NEAR.x + CARD_GAP - CARD_GAP * 0.1f * (cards.size - 5);
        }

        float y = 0;
        if (player == PlayerType.CURRENT_PLAYER) {
            y = 50f;
        }
        else {
            y = Field.OPPONENT_PLAYER_MONSTER_BASE.y;
        }

        float width = advance * (cards.size - 1) + Field.CARD_WIDTH_IN_CELL;
        float x = centerX - width / 2;
        for (Card card : cards) {
//            card.draw(sb, x, centerY, Field.CARD_WIDTH_IN_CELL, Field.CARD_HEIGHT_IN_CELL);
            card.positionInHand.set(x, y);
            x += advance;
        }
    }

    public Card getCard(int position) {
        return cards.get(position);
    }

    public void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            centerX--;
            refreshCardPositions();
            YGO.debug("centerX: " + centerX);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            centerX++;
            refreshCardPositions();
            YGO.debug("centerX: " + centerX);
        }

        Vector2 mousePos = Utils.getMousePos(playState.camera);
        boolean cardWasClicked = false;
        for (Card card : cards) {
            if (card.location == Location.HAND && card.contains(mousePos)) {
                card.isHovering = true;
                //detect click
                if (playState.clicked()) {
                    playState.showCardMenu(card);
                    cardWasClicked = true;
                }
            }
            else {
                card.isHovering = false;
            }
        }

        if (playState.clicked() && !cardWasClicked) {
            playState.hideCardMenu();
        }
    }

    public void draw(SpriteBatch sb) {
        for (Card card : cards) {
            card.draw(sb);
        }
    }
}
