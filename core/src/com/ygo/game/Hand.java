package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.Player;
import com.ygo.game.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Hand {
    private static final float DOWNSIZING_RATIO = 2f / 3f;
    public static final Vector2 CARD_SIZE_IN_HAND_NEAR = new Vector2(128 * .75f, 128).scl(.9f);
    public static final float CARD_GAP_NEAR = 15;
    public static final Vector2 CARD_SIZE_IN_HAND_FAR = new Vector2(128 * .75f, 128).scl(.9f * DOWNSIZING_RATIO);
    public static final float CARD_GAP_FAR = 15 * DOWNSIZING_RATIO;
    public static final int CARD_LIMIT = 7;

    private List<Card> cards = new ArrayList<>();

    private float centerX;
    private Player player;
    PlayState playState;

    public Hand(PlayState state, float centerX, Player player) {
        this.centerX = YGO.GAME_WIDTH * centerX;
        this.player = player;
        playState = state;
    }

    public void addCard(Card card, Player fromPerspective) {
        cards.add(card);
        card.setLocation(Location.Hand);
        refreshCardPositions(fromPerspective);
    }

    public void removeCard(Card card, Player fromPerspective) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId().equals(card.getId())) {
                cards.remove(i);
                break;
            }
        }
        refreshCardPositions(fromPerspective);
    }

    private void refreshCardPositions(Player fromPerspective) {
        Vector2 cardSize = fromPerspective == player ? CARD_SIZE_IN_HAND_NEAR : CARD_SIZE_IN_HAND_FAR;
        float gap = fromPerspective == player ? CARD_GAP_NEAR : CARD_GAP_FAR;

        //"advance" is the distance between cards plus the card width
        float advance;
        if (cards.size() <= 5) {
            advance = cardSize.x + gap;
        }
        else {
            advance = cardSize.x + gap - gap * 0.1f * (cards.size() - 5);
        }

        float y = fromPerspective == player ? 50f : 550;

        float width = advance * (cards.size() - 1) + Field.CARD_WIDTH_IN_CELL;
        float x = centerX - width / 2;
        for (Card card : cards) {
//            card.draw(sb, x, centerY, Field.CARD_WIDTH_IN_CELL, Field.CARD_HEIGHT_IN_CELL);
            card.positionInHand.set(x, y);
            x += advance;
        }
    }

    public boolean handleInput(float dt, Player playerId) {
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
            if (card.location == Location.Hand && card.contains(mousePos, playerId != player)) {
                if (!card.isHovering) {
                    YGO.debug(card.getName() + ": " + card.getId());
                }
                card.isHovering = true;
                //detect click
                if (playState.clicked()) {
                    Gdx.app.log("Hand", "Clicked " + card.nameId());
                    playState.showCardMenu(card);
                    cardWasClicked = true;
                }
            }
            else {
                card.isHovering = false;
            }
        }

        return cardWasClicked;
    }

    public void draw(SpriteBatch sb, Player playerId) {
        cards.forEach(c -> c.draw(sb, player != playerId));
    }
}
