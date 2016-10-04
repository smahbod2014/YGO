package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.ZoneType;

/**
 * Created by semahbod on 9/30/16.
 */
public class Field {

    private static final int CELL_WIDTH = YGO.WINDOW_WIDTH / 14;
    private static final int CELL_HEIGHT = YGO.WINDOW_WIDTH / 10; //TODO: Make this based on window height instead of width
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
    private Array<Card> p1MonsterZone = new Array<Card>();
    private Array<Card> p1SpellTrapZone = new Array<Card>();
    private Array<Card> p1Deck = new Array<Card>();
    private Array<Card> p1Graveyard = new Array<Card>();
    private Array<Card> p2MonsterZone = new Array<Card>();
    private Array<Card> p2SpellTrapZone = new Array<Card>();
    private Array<Card> p2Deck = new Array<Card>();
    private Array<Card> p2Graveyard = new Array<Card>();

    //temp variables
    private Card tempCard;

    public Field(float centerX, float centerY) {
        center = new Vector2();
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(YGO.camera.combined);
        sr.setColor(Color.WHITE);

        center.x = YGO.WINDOW_WIDTH * centerX - CELLS_IN_ROW * CELL_WIDTH / 2;
        center.y = YGO.WINDOW_HEIGHT * centerY - (CELL_HEIGHT * 4 + MIDDLE_DIVIDE) / 2;

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

    public void renderGrid() {
        sr.setProjectionMatrix(YGO.camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.RED);
        sr.line(YGO.WINDOW_WIDTH/2, YGO.WINDOW_HEIGHT, YGO.WINDOW_WIDTH/2, 0);
        sr.setColor(Color.WHITE);
        for (int i = 0; i < CELLS_IN_ROW; i++) {
            sr.rect(CURRENT_PLAYER_SPELL_TRAP_BASE.x + CELL_WIDTH * i,
                    CURRENT_PLAYER_SPELL_TRAP_BASE.y,
                    CELL_WIDTH, CELL_HEIGHT);
            sr.rect(CURRENT_PLAYER_MONSTER_BASE.x + CELL_WIDTH * i,
                    CURRENT_PLAYER_MONSTER_BASE.y,
                    CELL_WIDTH, CELL_HEIGHT);
            sr.rect(OPPONENT_PLAYER_SPELL_TRAP_BASE.x + CELL_WIDTH * i,
                    OPPONENT_PLAYER_SPELL_TRAP_BASE.y,
                    CELL_WIDTH, CELL_HEIGHT);
            sr.rect(OPPONENT_PLAYER_MONSTER_BASE.x + CELL_WIDTH * i,
                    OPPONENT_PLAYER_MONSTER_BASE.y,
                    CELL_WIDTH, CELL_HEIGHT);
        }
        sr.end();
    }

    public void renderCards(SpriteBatch sb) {
        Vector2 pos = getCardPositionInZone(PlayerType.CURRENT_PLAYER, ZoneType.SPELL_TRAP, 3);
        tempCard.draw(sb, pos.x, pos.y, CARD_WIDTH_IN_CELL, CARD_HEIGHT_IN_CELL);
    }
}
