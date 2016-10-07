package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.ZoneType;

/**
 * Created by semahbod on 9/30/16.
 */
public class Field {

    private static final int CELL_WIDTH = YGO.GAME_WIDTH / 14;
    private static final int CELL_HEIGHT = YGO.GAME_WIDTH / 10; //TODO: Make this based on window height instead of width
    private static final float CARD_OFFSET_X = CELL_WIDTH * 0.1f;
    private static final float CARD_OFFSET_Y = CELL_HEIGHT * 0.1f;
    public static final float CARD_WIDTH_IN_CELL = CELL_WIDTH - CARD_OFFSET_X * 2;
    public static final float CARD_HEIGHT_IN_CELL = CELL_HEIGHT - CARD_OFFSET_Y * 2;
    private static final int MIDDLE_DIVIDE = CELL_HEIGHT / 2;
    public static final Vector2 CURRENT_PLAYER_SPELL_TRAP_BASE = new Vector2();
    public static final Vector2 CURRENT_PLAYER_MONSTER_BASE = new Vector2();
    public static final Vector2 OPPONENT_PLAYER_SPELL_TRAP_BASE = new Vector2();
    public static final Vector2 OPPONENT_PLAYER_MONSTER_BASE = new Vector2();
    private static final int CELLS_IN_ROW = 5;

    private ShapeRenderer sr;
    private Vector2 center;
//    private Card[] p1MonsterZone = new Card[CELLS_IN_ROW];
//    private Card[] p1SpellTrapZone = new Card[CELLS_IN_ROW];
//    private Card[] p1Deck = new Card[CELLS_IN_ROW];
//    private Card[] p1Graveyard = new Card[CELLS_IN_ROW];
//    private Card[] p2MonsterZone = new Card[CELLS_IN_ROW];
//    private Card[] p2SpellTrapZone = new Card[CELLS_IN_ROW];
//    private Card[] p2Deck = new Card[CELLS_IN_ROW];
//    private Card[] p2Graveyard = new Card[CELLS_IN_ROW];
    private Card[][][] allCards = new Card[2][ZoneType.values().length][CELLS_IN_ROW];

    //temp variables
    private Card tempCard;
    private PerspectiveCamera perspectiveCamera;

    public Field(float centerX, float centerY) {
        center = new Vector2();
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(YGO.camera.combined);
        sr.setColor(Color.WHITE);

        perspectiveCamera = new PerspectiveCamera(45, YGO.GAME_WIDTH, YGO.GAME_HEIGHT);
        perspectiveCamera.position.set(0, 10, 10);
        perspectiveCamera.lookAt(0, 0, 0);
        perspectiveCamera.near = 1;
        perspectiveCamera.far = 300;
        perspectiveCamera.update();
        sr.setProjectionMatrix(perspectiveCamera.combined);

        center.x = YGO.GAME_WIDTH * centerX - CELLS_IN_ROW * CELL_WIDTH / 2;
        center.y = YGO.GAME_HEIGHT * centerY - (CELL_HEIGHT * 4 + MIDDLE_DIVIDE) / 2;

        CURRENT_PLAYER_SPELL_TRAP_BASE.set(center);
        CURRENT_PLAYER_MONSTER_BASE.set(CURRENT_PLAYER_SPELL_TRAP_BASE).add(0, CELL_HEIGHT);
        OPPONENT_PLAYER_SPELL_TRAP_BASE.set(CURRENT_PLAYER_MONSTER_BASE).add(0, CELL_HEIGHT + MIDDLE_DIVIDE);
        OPPONENT_PLAYER_MONSTER_BASE.set(OPPONENT_PLAYER_SPELL_TRAP_BASE).add(0, CELL_HEIGHT);

        tempCard = new Card("75646520", CardType.TRAP);
    }

    public Vector2 getCenter() {
        return center;
    }

    public float getWidth() {
        return CELLS_IN_ROW * CELL_WIDTH;
    }

    private Vector2 getCardPositionInZone(PlayerType player, ZoneType zone, int slot) {
        Vector2 base = new Vector2();
        if (player == PlayerType.CURRENT_PLAYER) {
            switch (zone) {
                case SPELL_TRAP:
                    base.add(CURRENT_PLAYER_SPELL_TRAP_BASE);
                    break;
                case MONSTER:
                    base.add(CURRENT_PLAYER_MONSTER_BASE);
                    break;
            }
            base.add(slot * CELL_WIDTH, 0).add(CARD_OFFSET_X, CARD_OFFSET_Y);
        }
        return base;
    }

    public void placeCardOnField(Card card, ZoneType destination, PlayerType playerSide, int cardPlayMode) {
        Card[] zone = getZone(destination, playerSide);
        int firstAvailable = getEmptyCell(zone);
        zone[firstAvailable] = card;
        card.location = Location.FIELD;
        card.playMode = cardPlayMode;
        YGO.debug("Card placed on field at " + getCardPositionInZone(playerSide, destination, firstAvailable));
        //this is where we would fire "onSummon" events
    }

    /**
     * Fetches an empty cell in a zone. Up to the user whether it's the first empty
     * cell, a random empty cell, etc.
     * @param zone
     * @return
     */
    private int getEmptyCell(Card[] zone) {
        // subject to different implementations
        for (int i = 0; i < zone.length; i++) {
            if (zone[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private Card[] getZone(ZoneType zone, PlayerType player) {
        return allCards[player.index][zone.index];
    }

    public void renderGrid() {
        int x = 100;
        int y = 0;
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        Gdx.gl.glViewport(x, y, w, h);
        sr.begin(ShapeRenderer.ShapeType.Line);
//        sr.setColor(Color.RED);
//        sr.line(YGO.GAME_WIDTH /2, YGO.GAME_HEIGHT, YGO.GAME_WIDTH /2, 0);
        sr.setColor(Color.WHITE);
//        for (int i = 0; i < CELLS_IN_ROW; i++) {
//            sr.rect(CURRENT_PLAYER_SPELL_TRAP_BASE.x + CELL_WIDTH * i,
//                    CURRENT_PLAYER_SPELL_TRAP_BASE.y,
//                    CELL_WIDTH, CELL_HEIGHT);
//            sr.rect(CURRENT_PLAYER_MONSTER_BASE.x + CELL_WIDTH * i,
//                    CURRENT_PLAYER_MONSTER_BASE.y,
//                    CELL_WIDTH, CELL_HEIGHT);
//            sr.rect(OPPONENT_PLAYER_SPELL_TRAP_BASE.x + CELL_WIDTH * i,
//                    OPPONENT_PLAYER_SPELL_TRAP_BASE.y,
//                    CELL_WIDTH, CELL_HEIGHT);
//            sr.rect(OPPONENT_PLAYER_MONSTER_BASE.x + CELL_WIDTH * i,
//                    OPPONENT_PLAYER_MONSTER_BASE.y,
//                    CELL_WIDTH, CELL_HEIGHT);
//        }
        float xoff = 0;
        sr.box(-5 + xoff, 0, 5, 10, 0, 10);
        sr.end();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void renderCards(SpriteBatch sb) {
//        Vector2 pos = getCardPositionInZone(PlayerType.CURRENT_PLAYER, ZoneType.SPELL_TRAP, 3);
//        tempCard.draw(sb, pos.x, pos.y, CARD_WIDTH_IN_CELL, CARD_HEIGHT_IN_CELL);
        for (int p = 0; p < allCards.length; p++) {
            Card[][] zones = allCards[p];
            for (int z = 0; z < zones.length; z++) {
                Card[] cards = zones[z];
                for (int c = 0; c < cards.length; c++) {
                    if (cards[c] != null) {
                        Vector2 pos = getCardPositionInZone(PlayerType.indexToPlayer(p), ZoneType.indexToZone(z), c);
                        cards[c].draw(sb, pos.x, pos.y, CARD_WIDTH_IN_CELL, CARD_HEIGHT_IN_CELL);
                    }
                }
            }
        }
    }
}
