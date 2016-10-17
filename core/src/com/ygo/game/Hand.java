package com.ygo.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;

import static com.ygo.game.YGO.debug;

public class Hand {
    private static final float DOWNSIZING_RATIO = 2f / 3f;
    public static final Vector2 CARD_SIZE_IN_HAND_NEAR = new Vector2(128 * .75f, 128).scl(.9f);
    public static final float CARD_GAP_NEAR = 15;
    public static final Vector2 CARD_SIZE_IN_HAND_FAR = new Vector2(128 * .75f, 128).scl(.9f * DOWNSIZING_RATIO);
    public static final float CARD_GAP_FAR = 15 * DOWNSIZING_RATIO;
    public static final int CARD_LIMIT = 7;

    private Array<Card> cards = new Array<Card>();

    private float centerX;
    private PlayerType player;
    PlayState playState;

    public Hand(PlayState state, float centerX, PlayerType player) {
        this.centerX = YGO.GAME_WIDTH * centerX;
        this.player = player;
        playState = state;
    }

    public void addCard(Card card, PlayerType fromPerspective) {
        cards.add(card);
        card.setLocation(Location.HAND);
        refreshCardPositions(fromPerspective);
    }

    public void removeCard(Card card, PlayerType fromPerspective) {
        cards.removeValue(card, false);
        refreshCardPositions(fromPerspective);
    }

    private void refreshCardPositions(PlayerType fromPerspective) {
        Vector2 cardSize = fromPerspective == player ? CARD_SIZE_IN_HAND_NEAR : CARD_SIZE_IN_HAND_FAR;
        float gap = fromPerspective == player ? CARD_GAP_NEAR : CARD_GAP_FAR;

        //"advance" is the distance between cards plus the card width
        float advance;
        if (cards.size <= 5) {
            advance = cardSize.x + gap;
        }
        else {
            advance = cardSize.x + gap - gap * 0.1f * (cards.size - 5);
        }

        float y = fromPerspective == player ? 50f : 600;

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

    public boolean handleInput(float dt, PlayerType playerId) {
//        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
//            centerX--;
//            refreshCardPositions();
//            YGO.debug("centerX: " + centerX);
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
//            centerX++;
//            refreshCardPositions();
//            YGO.debug("centerX: " + centerX);
//        }

        Vector2 mousePos = Utils.getMousePos(playState.camera);
        boolean cardWasClicked = false;
        for (Card card : cards) {
            if (card.location == Location.HAND && card.contains(mousePos, playerId != player)) {
                card.isHovering = true;
                //detect click
                if (playState.clicked()) {
                    debug("Clicked inside card!");
                    playState.showCardMenu(card);
                    cardWasClicked = true;
                }
            }
            else {
                card.isHovering = false;
            }
        }

        if (playerId == player && playState.clicked() && !cardWasClicked) {
//            playState.hideCardMenu();
        }

        return cardWasClicked;
    }

    public void draw(SpriteBatch sb, PlayerType playerId) {
        for (Card card : cards) {
            card.draw(sb, player != playerId);
        }
    }
}
