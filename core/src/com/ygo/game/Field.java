package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ygo.game.Types.ZoneType;

/**
 * Created by semahbod on 9/30/16.
 */
public class Field {

    private final int CELL_WIDTH = YGO.WINDOW_WIDTH / 14;
    private final int CELL_HEIGHT = YGO.WINDOW_WIDTH / 10; //TODO: Make this based on window height instead of width
    private final float CARD_OFFSET_X = CELL_WIDTH * 0.1f;
    private final float CARD_OFFSET_Y = CELL_HEIGHT * 0.1f;
    private final float CARD_WIDTH_IN_CELL = CELL_WIDTH - CARD_OFFSET_X * 2;
    private final float CARD_HEIGHT_IN_CELL = CELL_HEIGHT - CARD_OFFSET_Y * 2;
    private final int MIDDLE_DIVIDE = CELL_HEIGHT / 2;
    private final Vector2 CURRENT_PLAYER_SPELL_TRAP_BASE;
    private final Vector2 CURRENT_PLAYER_MONSTER_BASE;
    private final Vector2 OPPONENT_PLAYER_SPELL_TRAP_BASE;
    private final Vector2 OPPONENT_PLAYER_MONSTER_BASE;
    private final int CELLS_IN_ROW = 5;
    private final int CURRENT_PLAYER = 0;
    private final int OPPONENT_PLAYER = 1;

    private ShapeRenderer sr;
    private float xCoord, yCoord;
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

    public Field() {
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(YGO.camera.combined);
        sr.setColor(Color.WHITE);

        xCoord = YGO.WINDOW_WIDTH * 7 / 24 - CELLS_IN_ROW * CELL_WIDTH / 2;
        yCoord = YGO.WINDOW_HEIGHT / 2 - (CELL_HEIGHT * 4 + MIDDLE_DIVIDE) / 2;

        CURRENT_PLAYER_SPELL_TRAP_BASE = new Vector2(xCoord, yCoord);
        CURRENT_PLAYER_MONSTER_BASE = CURRENT_PLAYER_SPELL_TRAP_BASE.cpy().add(0, CELL_HEIGHT);
        OPPONENT_PLAYER_SPELL_TRAP_BASE = CURRENT_PLAYER_MONSTER_BASE.cpy().add(0, CELL_HEIGHT + MIDDLE_DIVIDE);
        OPPONENT_PLAYER_MONSTER_BASE = OPPONENT_PLAYER_SPELL_TRAP_BASE.cpy().add(0, CELL_HEIGHT);

        tempCard = new Card("75646520");
    }

    private Vector2 getCardPositionInZone(int player, ZoneType zone, int slot) {
        Vector2 base = new Vector2();
        if (player == CURRENT_PLAYER) {
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
//        float x = YGO.WINDOW_WIDTH / 2 - CELL_SIZE / 2;
//        float y = YGO.WINDOW_HEIGHT / 2 - CELL_SIZE / 2;
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.RED);
        sr.line(YGO.WINDOW_WIDTH/2, YGO.WINDOW_HEIGHT, YGO.WINDOW_WIDTH/2, 0);
        sr.setColor(Color.WHITE);
        for (int i = 0; i < CELLS_IN_ROW; i++) {
            float y = yCoord;
            sr.rect(xCoord + CELL_WIDTH * i, y, CELL_WIDTH, CELL_HEIGHT);
            y += CELL_HEIGHT;
            sr.rect(xCoord + CELL_WIDTH * i, y, CELL_WIDTH, CELL_HEIGHT);
            y += MIDDLE_DIVIDE + CELL_HEIGHT;
            sr.rect(xCoord + CELL_WIDTH * i, y, CELL_WIDTH, CELL_HEIGHT);
            y += CELL_HEIGHT;
            sr.rect(xCoord + CELL_WIDTH * i, y, CELL_WIDTH, CELL_HEIGHT);
        }
        sr.end();
    }

    public void renderCards(SpriteBatch sb) {
        Vector2 pos = getCardPositionInZone(CURRENT_PLAYER, ZoneType.SPELL_TRAP, 3);
        tempCard.draw(sb, pos.x, pos.y, CARD_WIDTH_IN_CELL, CARD_HEIGHT_IN_CELL);
    }
}
