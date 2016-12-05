package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.Player;
import com.ygo.game.Types.Zone;
import com.ygo.game.utils.Utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ygo.game.Types.Player.*;
import static com.ygo.game.Types.Zone.*;

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
    public Map<Player, Map<Zone, List<Cell>>> cells = new HashMap<>();
    public List<Cell> flatCells = new ArrayList<>();
    public static PerspectiveCamera perspectiveCamera;
    PlayState playState;

    //temp variables
    private Card tempCard;

    public Field(PlayState playState, float centerX, float centerY, Player playerId) {
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

    private void initCells(Player playerId) {
        Arrays.stream(Player.values())
                .forEach(p -> {
                    cells.put(p, new HashMap<>());
                    Arrays.stream(Zone.values()).forEach(z -> {
                        int quantity;
                        switch (z) {
                            case Monster:
                            case SpellTrap:
                                quantity = 5;
                                break;
                            case Pendulum:
                                quantity = 2;
                                break;
                            default:
                                quantity = 1;
                                break;
                        }
                        cells.get(p).put(z, new ArrayList<>(quantity));
                    });
                });

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
        Player p1 = Player.indexToPlayer(player1);
        Player p2 = Player.indexToPlayer(player2);
        cells.get(p1).get(ExtraDeck).add(new MultiCardCell(-5, 5, sideWidth, height, p1));
        cells.get(p1).get(Pendulum).add(new Cell(-5, 5 - height * 1 - padding * 1, sideWidth, height, p1));
        cells.get(p1).get(FieldSpell).add(new Cell(-5, 5 - height * 2 - padding * 2, sideWidth, height, p1));

        float gapFromSide = 10f * 0.03f;
        float topBottomMargin = 10f * 0.1f;
        float startX = -5 + sideWidth + gapFromSide;
        for (int i = 0; i < 5; i++) {
            cells.get(p1).get(SpellTrap).add(new Cell(startX + height * i, 5 - topBottomMargin, height, height, p1, i));
        }
        for (int i = 0; i < 5; i++) {
            cells.get(p1).get(Monster).add(new Cell(startX + height * i, 5 - topBottomMargin - height, height, height, p1, i));
        }

        float rightStartX = startX + height * 5 + gapFromSide;
        cells.get(p1).get(Deck).add(new MultiCardCell(rightStartX, 5, sideWidth, height, p1));
        cells.get(p1).get(Pendulum).add(new Cell(rightStartX, 5 - height * 1 - padding * 1, sideWidth, height, p1));
        cells.get(p1).get(Graveyard).add(new MultiCardCell(rightStartX, 5 - height * 2 - padding * 2, sideWidth, height, p1));
        cells.get(p1).get(Banished).add(new MultiCardCell(rightStartX + padding + sideWidth, 5 - height * 2 - padding * 2, sideWidth, height, p1));


        cells.get(p2).get(ExtraDeck).add(new MultiCardCell(rightStartX, 5 - height * 5 - padding * 5, sideWidth, height, p2));
        cells.get(p2).get(Pendulum).add(new Cell(rightStartX, 5 - height * 4 - padding * 4, sideWidth, height, p2));
        cells.get(p2).get(FieldSpell).add(new Cell(rightStartX, 5 - height * 3 - padding * 3, sideWidth, height, p2));

        cells.get(p2).get(Graveyard).add(new MultiCardCell(-5, 5 - height * 3 - padding * 3, sideWidth, height, p2));
        cells.get(p2).get(Banished).add(new MultiCardCell(-5 - padding - sideWidth, 5 - height * 3 - padding * 3, sideWidth, height, p2));
        cells.get(p2).get(Pendulum).add(new Cell(-5, 5 - height * 4 - padding * 4, sideWidth, height, p2));
        cells.get(p2).get(Deck).add(new MultiCardCell(-5, 5 - height * 5 - padding * 5, sideWidth, height, p2));

        for (int i = 0; i < 5; i++) {
            cells.get(p2).get(SpellTrap).add(new Cell(startX + height * i, -5 + topBottomMargin + height, height, height, p2, 4 - i));
        }
        for (int i = 0; i < 5; i++) {
            cells.get(p2).get(Monster).add(new Cell(startX + height * i, -5 + topBottomMargin + height * 2, height, height, p2, 4 - i));
        }

        //if we are player 1, we need to reverse player 2's monster/spelltrap zones
        if (playerId == PLAYER_1) {
            Collections.reverse(getZone(Monster, PLAYER_2));
            Collections.reverse(getZone(SpellTrap, PLAYER_2));
        }
        else {
            Collections.reverse(getZone(Monster, PLAYER_1));
            Collections.reverse(getZone(SpellTrap, PLAYER_1));
        }

        for (Player p : Player.values()) {
            for (Zone z : Zone.values()) {
                flatCells.addAll(getZone(z, p));
            }
        }
    }

    public float getWidth() {
        return CELLS_IN_ROW * CELL_WIDTH;
    }

    /** Returns the cell the card was placed in */
    public Cell placeCardOnField(Card card, Zone destination, Player playerSide, CardPlayMode cardPlayMode, Location location) {
        List<Cell> cells = getZone(destination, playerSide);
        int firstAvailable = getEmptyCell(cells);
        cells.get(firstAvailable).card = card;
        card.location = location;
        card.setZone(destination);
        card.overwritePlayMode(cardPlayMode);
        return cells.get(firstAvailable);
        //this is where we would fire "onSummon" events
    }

    public void placeCardsInZone(List<Card> cards, Zone destination, Player playerSide, CardPlayMode cardPlayMode, Location location) {
        MultiCardCell mc = (MultiCardCell) getZone(destination, playerSide).get(0);
        mc.cards.addAll(cards);
        for (Card card : cards) {
            card.location = location;
            card.setZone(destination);
            card.overwritePlayMode(cardPlayMode);
        }
    }

    public Cell getCellContainingCard(Card card) {
        Optional<Cell> cell = flatCells.stream().filter(c -> !(c instanceof MultiCardCell) && c.hasCard() && c.card.getId().equals(card.getId())).findFirst();
        if (cell.isPresent()) {
            return cell.get();
        }
        Gdx.app.error("Field", "Card " + card + " was not found in any cell");
        throw new RuntimeException();
    }

    /**
     * Fetches an empty cell in a zone. Up to the user whether it's the first empty
     * cell, a random empty cell, etc.
     * @param cells
     * @return
     */
    private int getEmptyCell(List<Cell> cells) {
        // subject to different implementations
        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).card == null) {
                return i;
            }
        }
        return -1;
    }

    public List<Cell> getZone(Zone zone, Player player) {
        return cells.get(player).get(zone);
    }

    public void renderGrid() {
        Utils.prepareViewport();
        sr.begin(ShapeRenderer.ShapeType.Line);
//        sr.setColor(Color.RED);
//        sr.box(-5 + xoff, 0, 5, 10, 0, 10);
        sr.setColor(Color.WHITE);
//        for (Player p : Player.values()) {
//            for (Zone z : Zone.values()) {
//                for (int i = 0; i < cells[p.index][z.index].length; i++) {
//                    if (cells[p.index][z.index][i] != null) {
//                        cells[p.index][z.index][i].draw(sr);
//                    }
//                }
//            }
//        }
        cells.forEach((p, z) -> {
            z.values().forEach(cells -> {
                cells.forEach(c -> {
                    if (c != null) {
                        c.draw(sr);
                    }
                });
            });
        });
        sr.end();
        Utils.revertViewport();
    }

    /**
     * Render the cards on the field from the perspective of <code>playerId</code>
     * @param playerId
     */
    public void renderCards(Player playerId, DecalBatch decalBatch2) {
        Utils.prepareViewport();
//        for (Player p : Player.values()) {
//            for (Zone z : Zone.values()) {
//                for (int i = 0; i < cells[p.index][z.index].length; i++) {
//                    if (cells[p.index][z.index][i] != null) {
//                        cells[p.index][z.index][i].drawCard(decalBatch, playerId);
//                    }
//                }
//            }
//        }
        cells.forEach((p, z) -> {
            z.values().forEach(cells -> {
                cells.forEach(c -> {
                    if (c != null) {
                        c.drawCard(decalBatch, playerId);
                    }
                });
            });
        });
        decalBatch.flush();
        Utils.revertViewport();
    }

    /**
     * Render the ATK and DEF of cards on the field from the perspective of <code>playerId</code>
     * @param playerId
     */
    public void renderStats(Player playerId, SpriteBatch batch) {
        Utils.prepareViewport();
        for (Player p : Player.values()) {
            for (Cell c : getZone(Monster, p)) {
                c.drawStats(batch, playerId, perspectiveCamera);
                Vector2 pos = Utils.worldPerspectiveToScreen(c.position.x + c.size.x / 2, c.position.y - c.size.y / 2, perspectiveCamera);
                YGO.cardStatsFont.draw(batch, "" + c.index, pos.x, pos.y);
            }
        }
        Utils.revertViewport();
    }



    public Card removeCard(Player player, Zone where, int which) {
        MultiCardCell mc = (MultiCardCell) getZone(where, player).get(0);
        if (which == TOP_CARD) {
            which = mc.cards.size() - 1;
        }
        return mc.cards.remove(which);
    }

    public boolean highlightCells() {
        int x = (int) (Gdx.graphics.getWidth() * 0.104f);
        int y = (int) (Gdx.graphics.getHeight() * 0.037f);
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        Ray ray = perspectiveCamera.getPickRay(Gdx.input.getX(), Gdx.input.getY(), x, y, w, h);
        boolean cardWasClicked = false;
        for (Player p : Player.values()) {
            for (Zone z : Zone.values()) {
                for (Cell c : getZone(z, p)) {
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

        return cardWasClicked;
    }

    public void clearTargeting() {
        for (Cell c : flatCells) {
            c.targetingCursorOn = false;
        }
    }

    public Cell getCellByIndex(Player player, Zone zone, int index) {
        for (Cell c : getZone(zone, player)) {
            if (c.index == index) {
                return c;
            }
        }
        return null;
    }

    @Deprecated
    public void drawDirectAttackLine(Player attacker, int cellIndexOrigin) {
        drawAttackLine(attacker, cellIndexOrigin, -1);
    }

    public void drawAttackLine(Player attacker, int cellIndexOrigin, int cellIndexDestination) {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.RED);
        Vector2 origin = getCellByIndex(attacker, Monster, cellIndexOrigin).getCenter();
        Vector2 dest;
        if (cellIndexDestination >= 0) {
            dest = getCellByIndex(attacker.getOpponent(), Monster, cellIndexDestination).getCenter();
        }
        else {
            dest = getCellByIndex(attacker.getOpponent(), SpellTrap, 2).getCenter();
        }
        sr.line(origin.x, 0, origin.y, dest.x, 0, dest.y);
        sr.end();
    }
}
