package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.ygo.game.GameStates.PlayState;
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
    private static final int CELLS_IN_ROW = 5;
    public static final int TOP_CARD = -1;

    private ShapeRenderer sr;
    private DecalBatch decalBatch;
    public Cell[][][] cells = new Cell[2][ZoneType.values().length][];
    public Array<Cell> flatCells = new Array<Cell>();
    public static PerspectiveCamera perspectiveCamera;
    PlayState playState;

    //temp variables
    private Card tempCard;

    public Field(PlayState playState, float centerX, float centerY, PlayerType playerId) {
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        this.playState = playState;

        perspectiveCamera = new PerspectiveCamera(45, YGO.GAME_WIDTH, YGO.GAME_HEIGHT);
        perspectiveCamera.position.set(0, 10, 10);
        perspectiveCamera.lookAt(0, 0, 0);
        perspectiveCamera.near = 1;
        perspectiveCamera.far = 300;
        perspectiveCamera.update();
        sr.setProjectionMatrix(perspectiveCamera.combined);
        decalBatch = new DecalBatch(new CameraGroupStrategy(perspectiveCamera));

        initCells(playerId);
    }

    private void initCells(PlayerType playerId) {
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
        int player1 = 0;
        int player2 = 1;
        if (playerId == PLAYER_2) {
            player1 = 1;
            player2 = 0;
        }
        PlayerType p1 = PlayerType.indexToPlayer(player1);
        PlayerType p2 = PlayerType.indexToPlayer(player2);
        cells[player1][EXTRA_DECK.index][0] = new MultiCardCell(-5, 5, sideWidth, height, p1);
        cells[player1][PENDULUM.index][0] = new Cell(-5, 5 - height * 1 - padding * 1, sideWidth, height, p1);
        cells[player1][FIELD_SPELL.index][0] = new Cell(-5, 5 - height * 2 - padding * 2, sideWidth, height, p1);

        float gapFromSide = 10f * 0.03f;
        float topBottomMargin = 10f * 0.1f;
        float startX = -5 + sideWidth + gapFromSide;
        for (int i = 0; i < 5; i++) {
            cells[player1][SPELL_TRAP.index][i] = new Cell(startX + height * i, 5 - topBottomMargin, height, height, p1, i);
        }
        for (int i = 0; i < 5; i++) {
            cells[player1][MONSTER.index][i] = new Cell(startX + height * i, 5 - topBottomMargin - height, height, height, p1, i);
        }

        float rightStartX = startX + height * 5 + gapFromSide;
        cells[player1][DECK.index][0] = new MultiCardCell(rightStartX, 5, sideWidth, height, p1);
        cells[player1][PENDULUM.index][1] = new Cell(rightStartX, 5 - height * 1 - padding * 1, sideWidth, height, p1);
        cells[player1][GRAVEYARD.index][0] = new MultiCardCell(rightStartX, 5 - height * 2 - padding * 2, sideWidth, height, p1);
        cells[player1][BANISHED.index][0] = new MultiCardCell(rightStartX + padding + sideWidth, 5 - height * 2 - padding * 2, sideWidth, height, p1);


        cells[player2][EXTRA_DECK.index][0] = new MultiCardCell(rightStartX, 5 - height * 5 - padding * 5, sideWidth, height, p2);
        cells[player2][PENDULUM.index][0] = new Cell(rightStartX, 5 - height * 4 - padding * 4, sideWidth, height, p2);
        cells[player2][FIELD_SPELL.index][0] = new Cell(rightStartX, 5 - height * 3 - padding * 3, sideWidth, height, p2);

        cells[player2][GRAVEYARD.index][0] = new MultiCardCell(-5, 5 - height * 3 - padding * 3, sideWidth, height, p2);
        cells[player2][BANISHED.index][0] = new MultiCardCell(-5 - padding - sideWidth, 5 - height * 3 - padding * 3, sideWidth, height, p2);
        cells[player2][PENDULUM.index][1] = new Cell(-5, 5 - height * 4 - padding * 4, sideWidth, height, p2);
        cells[player2][DECK.index][0] = new MultiCardCell(-5, 5 - height * 5 - padding * 5, sideWidth, height, p2);

        for (int i = 0; i < 5; i++) {
            cells[player2][SPELL_TRAP.index][i] = new Cell(startX + height * i, -5 + topBottomMargin + height, height, height, p2, 4 - i);
        }
        for (int i = 0; i < 5; i++) {
            cells[player2][MONSTER.index][i] = new Cell(startX + height * i, -5 + topBottomMargin + height * 2, height, height, p2, 4 - i);
        }

        //if we are player 1, we need to reverse player 2's monster/spelltrap zones
        if (playerId == PLAYER_1) {
            Utils.reverseArray(getZone(MONSTER, PLAYER_2));
            Utils.reverseArray(getZone(SPELL_TRAP, PLAYER_2));
        }
        else {
            Utils.reverseArray(getZone(MONSTER, PLAYER_1));
            Utils.reverseArray(getZone(SPELL_TRAP, PLAYER_1));
        }

        for (PlayerType p : PlayerType.values()) {
            for (ZoneType z : ZoneType.values()) {
                for (Cell c : getZone(z, p)) {
                    flatCells.add(c);
                }
            }
        }
    }

    public float getWidth() {
        return CELLS_IN_ROW * CELL_WIDTH;
    }

    public Cell getCellByIndex(ZoneType zone, int index) {
        for (PlayerType p : PlayerType.values()) {
            for (Cell c : getZone(zone, p)) {
                if (c.index == index) {
                    return c;
                }
            }
        }
        return null;
    }

    public void placeCardOnField(Card card, ZoneType destination, PlayerType playerSide, int cardPlayMode, Location location) {
        Cell[] zone = getZone(destination, playerSide);
        int firstAvailable = getEmptyCell(zone);
        zone[firstAvailable].card = card;
        card.location = location;
        card.playMode = cardPlayMode;
        //this is where we would fire "onSummon" events
    }

    public void placeCardsInZone(Array<Card> cards, ZoneType destination, PlayerType playerSide, int cardPlayMode, Location location) {
        Cell[] zone = getZone(destination, playerSide);
        MultiCardCell mc = (MultiCardCell) zone[0];
        mc.cards.addAll(cards);
        for (Card card : cards) {
            card.location = location;
            card.playMode = cardPlayMode;
        }
    }

    /**
     * Fetches an empty cell in a zone. Up to the user whether it's the first empty
     * cell, a random empty cell, etc.
     *
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
        Utils.prepareViewport();
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
        Utils.revertViewport();
    }

    /**
     * Render the cards on the field from the perspective of <code>playerId</code>
     * @param playerId
     */
    public void renderCards(PlayerType playerId, DecalBatch decalBatch2) {
        Utils.prepareViewport();
        for (PlayerType p : PlayerType.values()) {
            for (ZoneType z : ZoneType.values()) {
                for (int i = 0; i < cells[p.index][z.index].length; i++) {
                    if (cells[p.index][z.index][i] != null) {
                        cells[p.index][z.index][i].drawCard(decalBatch, playerId);
                    }
                }
            }
        }
        decalBatch.flush();
        Utils.revertViewport();
    }

    /**
     * Render the ATK and DEF of cards on the field from the perspective of <code>playerId</code>
     * @param playerId
     */
    public void renderStats(PlayerType playerId, SpriteBatch batch) {
        Utils.prepareViewport();
        for (PlayerType p : PlayerType.values()) {
            for (Cell c : getZone(MONSTER, p)) {
                c.drawStats(batch, playerId, perspectiveCamera);
                Vector2 pos = Utils.worldPerspectiveToScreen(c.position.x + c.size.x / 2, c.position.y - c.size.y / 2, perspectiveCamera);
                YGO.cardStatsFont.draw(batch, "" + c.index, pos.x, pos.y);
            }
        }
        Utils.revertViewport();
    }



    public Card removeCard(PlayerType player, ZoneType where, int which) {
        Cell[] zone = getZone(where, player);
        MultiCardCell mc = (MultiCardCell) zone[0];
        if (which == TOP_CARD) {
            which = mc.cards.size - 1;
        }
        return mc.cards.removeIndex(which);
    }

    public boolean highlightCells() {
        int x = (int) (Gdx.graphics.getWidth() * 0.104f);
        int y = (int) (Gdx.graphics.getHeight() * 0.037f);
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        Ray ray = perspectiveCamera.getPickRay(Gdx.input.getX(), Gdx.input.getY(), x, y, w, h);
        boolean cardWasClicked = false;
        for (PlayerType p : PlayerType.values()) {
            for (ZoneType z : ZoneType.values()) {
                for (Cell c : cells[p.index][z.index]) {
                    c.isHighlighted =  c.testRay(ray);
                    if (c.isHighlighted && c.hasCard() && playState.clicked()) {
                        if (c.owner == playState.playerId) {
                            playState.showFieldCardMenu(c.card, c);
                            cardWasClicked = true;
                        }
                        // The target cursor is hovering over this card (i.e. attack target)
                        if (c.targetingCursorOn) {
                            playState.confirmTarget(c);
                        }
                    }
                }
            }
        }

        if (playState.clicked() && !cardWasClicked) {
//            playState.hideCardMenu();
        }

        return cardWasClicked;
    }

    public void clearTargeting() {
        for (Cell c : flatCells) {
            c.targetingCursorOn = false;
        }
    }

    public Cell getCellByIndex(PlayerType player, ZoneType zone, int index) {
        Cell[] cells = getZone(zone, player);
        for (Cell c : cells) {
            if (c.index == index) {
                return c;
            }
        }
        return null;
    }

    public void drawDirectAttackLine(PlayerType attacker, int cellIndexOrigin) {
        drawAttackLine(attacker, cellIndexOrigin, -1);
    }

    public void drawAttackLine(PlayerType attacker, int cellIndexOrigin, int cellIndexDestination) {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.RED);
        Vector2 origin = getCellByIndex(attacker, MONSTER, cellIndexOrigin).getCenter();
        Vector2 dest;
        if (cellIndexDestination >= 0) {
            dest = getCellByIndex(attacker.getOpponent(), MONSTER, cellIndexDestination).getCenter();
        }
        else {
            dest = getCellByIndex(attacker.getOpponent(), SPELL_TRAP, 2).getCenter();
        }
        sr.line(origin.x, 0, origin.y, dest.x, 0, dest.y);
        sr.end();
    }
}
