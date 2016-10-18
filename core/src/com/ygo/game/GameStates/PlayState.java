package com.ygo.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;
import com.ygo.game.AttackData;
import com.ygo.game.Card;
import com.ygo.game.CardManager;
import com.ygo.game.Cell;
import com.ygo.game.Explosion;
import com.ygo.game.Messages.AttackMessage;
import com.ygo.game.MultiCardCell;
import com.ygo.game.TargetingCursor;
import com.ygo.game.TextFlash;
import com.ygo.game.DelayedEvents;
import com.ygo.game.Field;
import com.ygo.game.Hand;
import com.ygo.game.Messages.DrawMessage;
import com.ygo.game.Messages.GameInitializationMessage;
import com.ygo.game.Messages.NextPlayersTurnMessage;
import com.ygo.game.Messages.PhaseChangeMessage;
import com.ygo.game.Messages.SpellTrapSetMessage;
import com.ygo.game.Messages.SummonMessage;
import com.ygo.game.ServerListener;
import com.ygo.game.Tests.Tests;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.Phase;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.SummonType;
import com.ygo.game.Types.ZoneType;
import com.ygo.game.Utils;
import com.ygo.game.YGO;
import com.ygo.game.listeners.ActivateButtonListener;
import com.ygo.game.listeners.AttackButtonListener;
import com.ygo.game.listeners.NormalSummonButtonListener;
import com.ygo.game.listeners.SetButtonListener;

import static com.ygo.game.YGO.debug;
import static com.ygo.game.YGO.info;

/**
 * Created by semahbod on 10/8/16.
 */
public class PlayState extends GameState implements InputProcessor {

    private int maximumNormalSummons = 1;

    private enum Intent {NONE, ATTACKING}

    public OrthographicCamera camera;
    SpriteBatch batch;
    public Field field;
    Hand[] hands = new Hand[2];
    TextFlash phaseChangeTextFlash, damageTextFlash;

    Vector2 mouseDown = new Vector2();
    boolean mouseClicked = false;
    Skin skin;
    Stage stage;
    Table buttonTable, phaseTable;
    TextButton btnActivate, btnNormalSummon, btnSpecialSummon, btnSet, btnAttack,
            btnDrawPhase, btnStandbyPhase, btnMainPhase1, btnBattlePhase, btnMainPhase2, btnEndPhase;
    Array<TextButton> phaseButtons = new Array<TextButton>();
    Array<TargetingCursor> targetingCursors = new Array<TargetingCursor>();
    Array<Explosion> explosions = new Array<Explosion>();
    Card currentlySelectedCard;
    public PlayerType turnPlayer = PlayerType.PLAYER_1;
    public PlayerType playerId;
    Phase currentPhase;
    Server server;
    Client client;
    boolean isServer;
    /**
     * how many normal summons has the player conducted this turn?
     */
    int normalSummonsThisTurn;
    Intent intent;
    boolean attackTakingPlace;

    public PlayState(Server server, ServerListener serverListener, Client client) {
        this.server = server;
        this.client = client;
        this.isServer = server != null;

        if (isServer) {
            serverListener.playState = this;
            registerMessages(server);
            Gdx.graphics.setTitle("YGO: Player 1");
            playerId = PlayerType.PLAYER_1;
        }
        else {
            Gdx.graphics.setTitle("YGO: Player 2");
            playerId = PlayerType.PLAYER_2;
        }
        client.addListener(new ClientListener(this));
        registerMessages(client);

        initCommon();
        initGame();
        info("Initialization finished");
    }

    public PlayState(Client client) {
        this(null, null, client);
    }

    private void initCommon() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, YGO.GAME_WIDTH, YGO.GAME_HEIGHT);
        batch.setProjectionMatrix(camera.combined);

        field = new Field(this, 0.291f, 0.5f, playerId);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas("ui/uiskin.atlas"));
        stage = new Stage(new StretchViewport(YGO.GAME_WIDTH, YGO.GAME_HEIGHT, camera));

        initCardMenus();

        hands[0] = new Hand(this, 0.625f, PlayerType.PLAYER_1);
        hands[1] = new Hand(this, 0.625f, PlayerType.PLAYER_2);

        phaseButtons.add(btnDrawPhase);
        phaseButtons.add(btnStandbyPhase);
        phaseButtons.add(btnMainPhase1);
        phaseButtons.add(btnBattlePhase);
        phaseButtons.add(btnMainPhase2);
        phaseButtons.add(btnEndPhase);


        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);

        phaseChangeTextFlash = new TextFlash(camera);
        phaseChangeTextFlash.setPosition(camera.viewportWidth / 2 + 20, 75);
        damageTextFlash = new TextFlash(camera);
        damageTextFlash.setPosition(camera.viewportWidth / 2 + 20, 75);
        damageTextFlash.setColor(1, 0, 0, 1);
    }

    private void initCardMenus() {
        buttonTable = new Table();
        btnActivate = new TextButton("Activate", skin);
        btnActivate.addListener(new ActivateButtonListener(this));

        btnNormalSummon = new TextButton("Normal Summon", skin);
        btnNormalSummon.addListener(new NormalSummonButtonListener(this));

        btnSet = new TextButton("Set", skin);
        btnSet.addListener(new SetButtonListener(this));

        btnAttack = new TextButton("Attack", skin);
        btnAttack.addListener(new AttackButtonListener(this));

        buttonTable.setVisible(false);

        stage.addActor(buttonTable);

        phaseTable = new Table();

        btnDrawPhase = new TextButton("DP", skin);
        btnStandbyPhase = new TextButton("SP", skin);
        btnMainPhase1 = new TextButton("M1", skin);
        btnBattlePhase = new TextButton("BP", skin);
        btnMainPhase2 = new TextButton("M2", skin);
        btnEndPhase = new TextButton("EP", skin);
        setupPhaseButtonListener(btnBattlePhase, Phase.BATTLE_PHASE);
        setupPhaseButtonListener(btnMainPhase2, Phase.MAIN_PHASE_2);
        setupPhaseButtonListener(btnEndPhase, Phase.END_PHASE);

        loc = new Vector2(805f, 385f);
        float buttonWidth = 60;
        phaseTable.setPosition(loc.x, loc.y, Align.center);
        phaseTable.add(btnDrawPhase).width(buttonWidth).padRight(15);
        phaseTable.add(btnStandbyPhase).width(buttonWidth).padRight(15);
        phaseTable.add(btnMainPhase1).width(buttonWidth).padRight(15);
        phaseTable.add(btnBattlePhase).width(buttonWidth).padRight(15);
        phaseTable.add(btnMainPhase2).width(buttonWidth).padRight(15);
        phaseTable.add(btnEndPhase).width(buttonWidth).padRight(15);

        stage.addActor(phaseTable);
    }

    private void setupPhaseButtonListener(TextButton button, final Phase newPhase) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                client.sendTCP(new PhaseChangeMessage(newPhase));
            }
        });
    }

    private void initGame() {
        if (!isServer)
            return;

        Array<String> p1Deck = new Array<String>();
        Array<String> p2Deck = new Array<String>();

        CardManager.clearDuplicatesHistory();
        for (int i = 0; i < 10; i++) {
            p1Deck.add(CardManager.getRandomNoDuplicates().id);
            if (i < 3)
                debug("Added " + p1Deck.peek() + "to P1 deck");
        }

        CardManager.clearDuplicatesHistory();
        for (int i = 0; i < 15; i++) {
            p2Deck.add(CardManager.getRandomNoDuplicates().id);
            if (i < 3)
                debug("Added " + p2Deck.peek() + "to P2 deck");
        }

        server.sendToAllTCP(new GameInitializationMessage(p1Deck, p2Deck));

        int cardsToDraw = 5;
        for (int i = 0; i < cardsToDraw; i++) {
            DelayedEvents.schedule(new Runnable() {
                @Override
                public void run() {
                    server.sendToAllTCP(new DrawMessage(PlayerType.PLAYER_1));
                    server.sendToAllTCP(new DrawMessage(PlayerType.PLAYER_2));
                }
            }, 1 + i * 0.5f);
        }

        DelayedEvents.schedule(new Runnable() {
            @Override
            public void run() {
                server.sendToAllTCP(new NextPlayersTurnMessage(PlayerType.PLAYER_1));
            }
        }, 1 + cardsToDraw * 0.5f);

        DelayedEvents.schedule(new Runnable() {
            @Override
            public void run() {
                server.sendToAllTCP(new PhaseChangeMessage(Phase.DRAW_PHASE));
            }
        }, 1 + cardsToDraw * 0.5f + 1);

    }

    Vector2 loc = new Vector2();
    Vector2 loc2 = new Vector2();

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            phaseChangeTextFlash.flash("Draw Phase", 0.33f, 0.67f);
            debug("Flashing message");
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            loc.x -= 5;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            loc.x += 5;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            loc.y += 5;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            loc.y -= 5;
        }
        phaseTable.setPosition(loc.x, loc.y);
        if (!loc.equals(loc2)) {
            debug(loc.toString());
        }
        loc2 = loc.cpy();

        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && playerId == PlayerType.PLAYER_1) {
            initCommon();
            initGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            debug("Mouse pos: " + Utils.getMousePos(camera));
        }


        Tests.input(dt);
        DelayedEvents.update(dt);
        for (TargetingCursor tc : targetingCursors) {
            tc.update(dt);
        }
        if (!hands[0].handleInput(dt, playerId) && !hands[1].handleInput(dt, playerId) && !field.highlightCells() && clicked()) {
            hideCardMenu();
            clearAllTargeting();
        }
        for (int i = explosions.size - 1; i >= 0; i--) {
            Explosion e = explosions.get(i);
            e.update(dt);
            if (e.isDead) {
                explosions.removeValue(e, true);
            }
        }
        stage.act(dt);
        phaseChangeTextFlash.update(dt);
        damageTextFlash.update(dt);
    }

    @Override
    public void render() {
        field.renderGrid();
        field.renderCards(playerId);
        batch.begin();
        field.renderStats(playerId, batch);
        hands[0].draw(batch, playerId);
        hands[1].draw(batch, playerId);
        for (TargetingCursor tc : targetingCursors) {
            tc.render(batch);
        }
        for (Explosion e : explosions) {
            e.render(batch);
        }
        batch.end();

        stage.draw();
        phaseChangeTextFlash.render();
        damageTextFlash.render();

        //reset mouse click event
        mouseClicked = false;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (isServer) {
            server.stop();
        }
        client.stop();
    }

    private void addButtonToTable(TextButton button) {
        buttonTable.add(button).width(140).fill().row();
    }

    private boolean hasMonsters(PlayerType player) {
        for (Cell c : field.getZone(ZoneType.MONSTER, player)) {
            if (c.hasCard()) {
                return true;
            }
        }
        return false;
    }

    private boolean canNormalSummon() {
        return normalSummonsThisTurn < maximumNormalSummons;
    }

    public void showCardMenu(Card card) {
        currentlySelectedCard = card;
        buttonTable.clear();
        buttonTable.setVisible(true);
        buttonTable.setPosition(Utils.getMousePos(camera).x + 40, Utils.getMousePos(camera).y + 50);
        if (card.isMonster()) {
            if (canNormalSummon()) {
                addButtonToTable(btnNormalSummon);
                addButtonToTable(btnSet);
            }
            debug("Monster card clicked (" + card.id + ")");
        }
        else if (card.isSpell()) {
            addButtonToTable(btnActivate);
            addButtonToTable(btnSet);
            debug("Spell card clicked (" + card.id + ")");
        }
        else if (card.isTrap()) {
            addButtonToTable(btnSet);
            debug("Trap card clicked (" + card.id + ")");
        }
        else {
            debug("ERROR: Unknown card type clicked: (" + card.id + ")");
        }
    }

    public void showFieldCardMenu(Card card) {
        currentlySelectedCard = card;
        buttonTable.clear();
        buttonTable.setVisible(true);
        buttonTable.setPosition(Utils.getMousePos(camera).x + 40, Utils.getMousePos(camera).y + 50);
        if (card.isMonster()) {
            if (currentPhase == Phase.BATTLE_PHASE && card.canAttack()) {
                buttonTable.add(btnAttack).width(100);
            }
        }
    }

    public void hideCardMenu() {
        buttonTable.setVisible(false);
    }

    public void performNormalSummon() {
        performSummon(SummonType.NORMAL_SUMMON, CardPlayMode.FACE_UP | CardPlayMode.ATTACK_MODE);
        normalSummonsThisTurn++;
    }

    public void performEffectActivation() {
        info("performEffectActivation not implemented");
    }

    //TODO: This will later take a parameter indicating where the card is being set from (hand, deck, graveyard...)
    public void performSet() {
        if (currentlySelectedCard.isMonster()) {
            performSummon(SummonType.SET, CardPlayMode.FACE_DOWN | CardPlayMode.DEFENSE_MODE);
        }
        else {
            SpellTrapSetMessage m = new SpellTrapSetMessage(playerId.index, Location.HAND.index, currentlySelectedCard.id);
            client.sendTCP(m);
        }
    }

    private void performSummon(SummonType summonType, int cardPlayMode) {
//        Hand hand = hands[turnPlayer.index];
//        if (currentlySelectedCard.location == Location.HAND) {
//            hand.removeCard(currentlySelectedCard, turnPlayer);
//        }
//        field.placeCardOnField(currentlySelectedCard, ZoneType.MONSTER, turnPlayer, cardPlayMode, Location.FIELD);

        //TODO: pass location as parameter
        SummonMessage m = new SummonMessage(playerId.index, Location.HAND.index, currentlySelectedCard.id, summonType.index, cardPlayMode);
        client.sendTCP(m);
    }

    public boolean clicked() {
        return mouseClicked;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            System.out.println("Pointer at: " + Utils.getMousePos(camera));
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float sx = Utils.convertX(screenX);
        float sy = Utils.convertY(screenY);
        if (button == Input.Buttons.LEFT) {
            mouseDown.set(sx, sy);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float sx = Utils.convertX(screenX);
        float sy = Utils.convertY(screenY);
        Vector2 current = new Vector2(sx, sy);
        if (button == Input.Buttons.LEFT && current.dst(mouseDown) <= 5) {
            mouseClicked = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private void registerMessages(EndPoint ep) {
        Kryo k = ep.getKryo();
        k.register(Object[].class);
        k.register(Array.class);
        k.register(GameInitializationMessage.class);
        k.register(DrawMessage.class);
        k.register(SummonMessage.class);
        k.register(SpellTrapSetMessage.class);
        k.register(PhaseChangeMessage.class);
        k.register(NextPlayersTurnMessage.class);
        k.register(AttackMessage.class);
    }

    private void drawCard(PlayerType player) {
        Card card = field.removeCard(player, ZoneType.DECK, Field.TOP_CARD);
        hands[player.index].addCard(card, playerId);
        debug(player.toString() + " drew " + card.id);
    }

    private void setPhaseButtonVisibleButOthersNot(TextButton button) {
        for (TextButton b : phaseButtons) {
            if (b != button) {
                b.setVisible(false);
            }
            else {
                b.setTouchable(Touchable.disabled);
                b.setVisible(true);
            }
        }
    }

    private void showPhaseButton(TextButton... buttons) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setTouchable(Touchable.enabled);
            buttons[i].setVisible(true);
        }
    }

    private void advanceToPhase(final Phase next, float delay) {
        DelayedEvents.schedule(new Runnable() {
            @Override
            public void run() {
                client.sendTCP(new PhaseChangeMessage(next));
            }
        }, delay);
    }

    public void handleGameInitializationMessage(GameInitializationMessage m) {
        Array<Card> p1Deck = new Array<Card>();
        Array<Card> p2Deck = new Array<Card>();

        for (String id : m.p1Deck) {
            p1Deck.add(CardManager.get(id).copy());
        }

        for (String id : m.p2Deck) {
            p2Deck.add(CardManager.get(id).copy());
        }

        field.placeCardsInZone(p1Deck, ZoneType.DECK, PlayerType.PLAYER_1, CardPlayMode.FACE_DOWN, Location.DECK);
        field.placeCardsInZone(p2Deck, ZoneType.DECK, PlayerType.PLAYER_2, CardPlayMode.FACE_DOWN, Location.DECK);

        for (PlayerType p : PlayerType.values()) {
            for (ZoneType z : ZoneType.values()) {
                for (Cell c : field.getZone(z, p)) {
                    c.card = null;
                }
            }
        }

        field.placeCardOnField(CardManager.get("93013676").copy(), ZoneType.MONSTER, PlayerType.PLAYER_1, CardPlayMode.FACE_UP, Location.FIELD);
        field.placeCardOnField(CardManager.get("88819587").copy(), ZoneType.MONSTER, PlayerType.PLAYER_2, CardPlayMode.FACE_UP, Location.FIELD);
        field.placeCardOnField(CardManager.get("yugi/15025844").copy(), ZoneType.MONSTER, PlayerType.PLAYER_2, CardPlayMode.FACE_UP, Location.FIELD);

        turnPlayer = PlayerType.PLAYER_1;
    }

    public void handleDrawMessage(DrawMessage m) {
        drawCard(PlayerType.valueOf(m.player));
    }

    public void handleSummonMessage(SummonMessage m) {
        PlayerType player = PlayerType.indexToPlayer(m.player);
        Card card = CardManager.get(m.cardId);
        if (Location.indexToLocation(m.location) == Location.HAND) {
            Hand hand = hands[player.index];
            hand.removeCard(card, playerId);
        }
        field.placeCardOnField(card, ZoneType.MONSTER, player, m.cardPlayMode, Location.FIELD);
    }

    public void handleSpellTrapSetMessage(SpellTrapSetMessage m) {
        PlayerType player = PlayerType.indexToPlayer(m.player);
        Card card = CardManager.get(m.cardId);
        if (Location.indexToLocation(m.location) == Location.HAND) {
            Hand hand = hands[player.index];
            hand.removeCard(card, playerId);
        }
        field.placeCardOnField(card, ZoneType.SPELL_TRAP, player, CardPlayMode.FACE_DOWN, Location.FIELD);
    }

    public void handlePhaseChangeMessage(PhaseChangeMessage m) {
        currentPhase = Phase.valueOf(m.newPhase);
        phaseChangeTextFlash.flash(currentPhase.toString(), 1f / 3f, 2f / 3f);
        if (playerId == turnPlayer) {
            if (currentPhase == Phase.DRAW_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnDrawPhase);
                // draw
                client.sendTCP(new DrawMessage(playerId));
                //TODO have to check if there are no events that occur during draw phase
                advanceToPhase(Phase.STANDBY_PHASE, 1);
            }
            else if (currentPhase == Phase.STANDBY_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnStandbyPhase);
                //TODO have to check if there are no events that occur during standby phase
                advanceToPhase(Phase.MAIN_PHASE_1, 1);
            }
            else if (currentPhase == Phase.MAIN_PHASE_1) {
                setPhaseButtonVisibleButOthersNot(btnMainPhase1);
                showPhaseButton(btnBattlePhase, btnEndPhase);
            }
            else if (currentPhase == Phase.BATTLE_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnBattlePhase);
                showPhaseButton(btnMainPhase2, btnEndPhase);
            }
            else if (currentPhase == Phase.MAIN_PHASE_2) {
                setPhaseButtonVisibleButOthersNot(btnMainPhase2);
                showPhaseButton(btnEndPhase);
            }
            else {
                setPhaseButtonVisibleButOthersNot(btnEndPhase);
            }
        }
        else {
            if (currentPhase == Phase.DRAW_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnDrawPhase);
            }
            else if (currentPhase == Phase.STANDBY_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnStandbyPhase);
            }
            else if (currentPhase == Phase.MAIN_PHASE_1) {
                setPhaseButtonVisibleButOthersNot(btnMainPhase1);
            }
            else if (currentPhase == Phase.BATTLE_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnBattlePhase);
            }
            else if (currentPhase == Phase.MAIN_PHASE_2) {
                setPhaseButtonVisibleButOthersNot(btnMainPhase2);
            }
            else {
                setPhaseButtonVisibleButOthersNot(btnEndPhase);
            }
        }
    }

    public void handleNextPlayersTurnMessage(NextPlayersTurnMessage m) {
        turnPlayer = PlayerType.valueOf(m.player);
        phaseChangeTextFlash.flash(turnPlayer.toString() + "'s Turn", 1f / 3f, 2f / 3f);
    }

    public void handleAttackMessage(AttackMessage m) {
        final PlayerType conducting = PlayerType.valueOf(m.player);
        Cell[] zone = field.getZone(ZoneType.MONSTER, conducting.getOpponent());
        int index = m.targetCell;
        if (conducting == playerId) {
            intent = Intent.NONE;
        }
        final Cell cell = zone[index];
        explosions.add(new Explosion(cell));
        damageTextFlash.flash("-" + CardManager.get(m.cardId).atk, 1f / 3, 2f / 3);
        AttackData.attackTakingPlace = true;
        AttackData.attacker = conducting;
//        AttackData.cellOrigin =
        DelayedEvents.schedule(new Runnable() {
            @Override
            public void run() {
                Card card = cell.card;
                card.location = Location.GRAVEYARD;
                card.playMode = CardPlayMode.FACE_UP;
                Cell[] graveyard = field.getZone(ZoneType.GRAVEYARD, conducting.getOpponent());
                MultiCardCell mc = (MultiCardCell) graveyard[0];
                mc.cards.add(cell.card);
                cell.card = null;
            }
        }, 1);
    }

    public void displayAttackTargets() {
        if (!hasMonsters(playerId.getOpponent())) {
            damageTextFlash.flash("-" + currentlySelectedCard.atk, 1f / 3, 2f / 3);
            currentlySelectedCard.attacksThisTurn++;
        }
        else {
            intent = Intent.ATTACKING;
            for (Cell c : field.getZone(ZoneType.MONSTER, playerId.getOpponent())) {
                if (c.hasCard()) {
                    targetingCursors.add(new TargetingCursor(c));
                    c.targetingCursorOn = true;
                }
            }
        }
    }

    public void confirmTarget(Cell cell) {
        if (intent == Intent.NONE) {
            return;
        }

        //TODO: NEED TO INDICATE THE CELL THAT WE ARE ATTACKING FROM
        if (intent == Intent.ATTACKING) {
            client.sendTCP(new AttackMessage(playerId, cell.index, currentlySelectedCard.id));
            clearAllTargeting();
            currentlySelectedCard.attacksThisTurn++;
        }
    }

    private void clearAllTargeting() {
        targetingCursors.clear();
        field.clearTargeting();
    }
}
