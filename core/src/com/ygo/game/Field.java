package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.ZoneType;


import static com.ygo.game.Types.PlayerType.*;
import static com.ygo.game.Types.ZoneType.*;

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
    private DecalBatch decalBatch;
    private Vector2 center;
    private Card[][][] allCards = new Card[2][ZoneType.values().length][CELLS_IN_ROW];
    Cell[][][] cells = new Cell[2][ZoneType.values().length][];

    //temp variables
    private Card tempCard;
    private PerspectiveCamera perspectiveCamera;

    public Field(float centerX, float centerY) {
        center = new Vector2();
        sr = new ShapeRenderer();

        perspectiveCamera = new PerspectiveCamera(45, YGO.GAME_WIDTH, YGO.GAME_HEIGHT);
        perspectiveCamera.position.set(0, 10, 10);
        perspectiveCamera.lookAt(0, 0, 0);
        perspectiveCamera.near = 1;
        perspectiveCamera.far = 300;
        perspectiveCamera.update();
        sr.setProjectionMatrix(perspectiveCamera.combined);
        decalBatch = new DecalBatch(new CameraGroupStrategy(perspectiveCamera));

        center.x = YGO.GAME_WIDTH * centerX - CELLS_IN_ROW * CELL_WIDTH / 2;
        center.y = YGO.GAME_HEIGHT * centerY - (CELL_HEIGHT * 4 + MIDDLE_DIVIDE) / 2;

        CURRENT_PLAYER_SPELL_TRAP_BASE.set(center);
        CURRENT_PLAYER_MONSTER_BASE.set(CURRENT_PLAYER_SPELL_TRAP_BASE).add(0, CELL_HEIGHT);
        OPPONENT_PLAYER_SPELL_TRAP_BASE.set(CURRENT_PLAYER_MONSTER_BASE).add(0, CELL_HEIGHT + MIDDLE_DIVIDE);
        OPPONENT_PLAYER_MONSTER_BASE.set(OPPONENT_PLAYER_SPELL_TRAP_BASE).add(0, CELL_HEIGHT);

        initCells();
    }

    private void initCells() {
        for (PlayerType p : PlayerType.values()) {
            for (ZoneType z : ZoneType.values()) {
                int quantity;
                switch (z) {
                    case MONSTER:
                    case SPELL_TRAP:
                        quantity = 5;
                        break;
                    case PENDULUM:
                        quantity = 2;
                        break;
                    default:
                        quantity = 1;
                        break;
                }

                cells[p.index][z.index] = new Cell[quantity];
            }
        }

        float sideWidth = 1.3f;
        float padding = 10f * .02f;
        float height = 10f / 6.5f;
        Cell.cardSize.set(sideWidth * 0.8f, height * 0.9f);
        cells[CURRENT_PLAYER.index][EXTRA_DECK.index][0]    = new MultiCardCell(-5, 5, sideWidth, height);
        cells[CURRENT_PLAYER.index][PENDULUM.index][0]      = new Cell(-5, 5-height*1-padding*1, sideWidth, height);
        cells[CURRENT_PLAYER.index][FIELD_SPELL.index][0]   = new Cell(-5, 5-height*2-padding*2, sideWidth, height);

        float gapFromSide = 10f * 0.03f;
        float topBottomMargin = 10f * 0.1f;
        float startX = -5 + sideWidth + gapFromSide;
        for (int i = 0; i < 5; i++) {
            cells[CURRENT_PLAYER.index][SPELL_TRAP.index][i]    = new Cell(startX + height * i, 5 - topBottomMargin, height, height);
        }
        for (int i = 0; i < 5; i++) {
            cells[CURRENT_PLAYER.index][MONSTER.index][i]    = new Cell(startX + height * i, 5 - topBottomMargin - height, height, height);
        }

        float rightStartX = startX + height * 5 + gapFromSide;
        cells[CURRENT_PLAYER.index][DECK.index][0]          = new MultiCardCell(rightStartX, 5, sideWidth, height);
        cells[CURRENT_PLAYER.index][PENDULUM.index][1]      = new Cell(rightStartX, 5-height*1-padding*1, sideWidth, height);
        cells[CURRENT_PLAYER.index][GRAVEYARD.index][0]     = new MultiCardCell(rightStartX, 5-height*2-padding*2, sideWidth, height);
        cells[CURRENT_PLAYER.index][BANISHED.index][0]     = new MultiCardCell(rightStartX + padding + sideWidth, 5-height*2-padding*2, sideWidth, height);


        cells[OPPONENT_PLAYER.index][EXTRA_DECK.index][0]    = new MultiCardCell(rightStartX, 5-height*5-padding*5, sideWidth, height);
        cells[OPPONENT_PLAYER.index][PENDULUM.index][0]      = new Cell(rightStartX, 5-height*4-padding*4, sideWidth, height);
        cells[OPPONENT_PLAYER.index][FIELD_SPELL.index][0]   = new Cell(rightStartX, 5-height*3-padding*3, sideWidth, height);

        cells[OPPONENT_PLAYER.index][GRAVEYARD.index][0]    = new MultiCardCell(-5, 5-height*3-padding*3, sideWidth, height);
        cells[OPPONENT_PLAYER.index][BANISHED.index][0]     = new MultiCardCell(-5 - padding - sideWidth, 5-height*3-padding*3, sideWidth, height);
        cells[OPPONENT_PLAYER.index][PENDULUM.index][1]     = new Cell(-5, 5-height*4-padding*4, sideWidth, height);
        cells[OPPONENT_PLAYER.index][DECK.index][0]         = new MultiCardCell(-5, 5-height*5-padding*5, sideWidth, height);

        for (int i = 0; i < 5; i++) {
            cells[OPPONENT_PLAYER.index][SPELL_TRAP.index][i]    = new Cell(startX + height * i, -5 + topBottomMargin + height, height, height);
        }
        for (int i = 0; i < 5; i++) {
            cells[OPPONENT_PLAYER.index][MONSTER.index][i]    = new Cell(startX + height * i, -5 + topBottomMargin + height * 2, height, height);
        }
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
        Cell[] zone = getZone(destination, playerSide);
        int firstAvailable = getEmptyCell(zone);
        zone[firstAvailable].card = card;
        card.location = Location.FIELD;
        card.playMode = cardPlayMode;
        //this is where we would fire "onSummon" events
    }

    /**
     * Fetches an empty cell in a zone. Up to the user whether it's the first empty
     * cell, a random empty cell, etc.
     * @param zone
     * @return
     */
    private int getEmptyCell(Cell[] zone) {
        // subject to different implementations
        for (int i = 0; i < zone.length; i++) {
            if (zone[i].card == null) {
                return i;
            }
        }
        return -1;
    }

    public Cell[] getZone(ZoneType zone, PlayerType player) {
        return cells[player.index][zone.index];
    }

    public void renderGrid() {
        prepareViewport();
        sr.begin(ShapeRenderer.ShapeType.Line);
//        sr.setColor(Color.RED);
//        sr.box(-5 + xoff, 0, 5, 10, 0, 10);
        sr.setColor(Color.WHITE);
        for (PlayerType p : PlayerType.values()) {
            for (ZoneType z : ZoneType.values()) {
                for (int i = 0; i < cells[p.index][z.index].length; i++) {
                    if (cells[p.index][z.index][i] != null) {
                        cells[p.index][z.index][i].draw(sr);
                    }
                }
            }
        }
        sr.end();
        revertViewport();
    }

    public void renderCards() {
        prepareViewport();
        for (PlayerType p : PlayerType.values()) {
            for (ZoneType z : ZoneType.values()) {
                for (int i = 0; i < cells[p.index][z.index].length; i++) {
                    if (cells[p.index][z.index][i] != null) {
                        cells[p.index][z.index][i].drawCard(decalBatch, p);
                    }
                }
            }
        }
        decalBatch.flush();
        revertViewport();
    }

    private void prepareViewport() {
        int x = (int) (Gdx.graphics.getWidth() * 0.104f);
        int y = (int) (Gdx.graphics.getHeight() * 0.037f);
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        Gdx.gl.glViewport(x, y, w, h);
    }

    private void revertViewport() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}
