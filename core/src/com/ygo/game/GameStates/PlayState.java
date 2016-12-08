package com.ygo.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;
import com.ygo.game.AttackSwordVisual;
import com.ygo.game.Cannonball;
import com.ygo.game.Card;
import com.ygo.game.CardManager;
import com.ygo.game.Cell;
import com.ygo.game.DelayedEvents;
import com.ygo.game.Effect;
import com.ygo.game.Explosion;
import com.ygo.game.Field;
import com.ygo.game.Hand;
import com.ygo.game.Lifepoints;
import com.ygo.game.Messages.AttackInitiationMessage;
import com.ygo.game.Messages.AttackMessage;
import com.ygo.game.Messages.BattlePositionChangeMessage;
import com.ygo.game.Messages.CardActivationMessage;
import com.ygo.game.Messages.DirectAttackInitiationMessage;
import com.ygo.game.Messages.DirectAttackMessage;
import com.ygo.game.Messages.DrawMessage;
import com.ygo.game.Messages.GameInitializationMessage;
import com.ygo.game.Messages.NextPlayersTurnMessage;
import com.ygo.game.Messages.PhaseChangeMessage;
import com.ygo.game.Messages.RetaliatoryDamageMessage;
import com.ygo.game.Messages.SpellTrapSetMessage;
import com.ygo.game.Messages.SummonMessage;
import com.ygo.game.Messages.TestMessage;
import com.ygo.game.MultiCardCell;
import com.ygo.game.Pair;
import com.ygo.game.ServerListener;
import com.ygo.game.TargetingCursor;
import com.ygo.game.Tests.Tests;
import com.ygo.game.TextFlash;
import com.ygo.game.TweenAnimations;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.DamageType;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.Phase;
import com.ygo.game.Types.Player;
import com.ygo.game.Types.ResponseDialog;
import com.ygo.game.Types.SummonType;
import com.ygo.game.Types.Zone;
import com.ygo.game.YGO;
import com.ygo.game.buffs.Buff;
import com.ygo.game.db.CardDao;
import com.ygo.game.listeners.ActivateButtonListener;
import com.ygo.game.listeners.AttackButtonListener;
import com.ygo.game.listeners.ChangePositionListener;
import com.ygo.game.listeners.NormalSummonButtonListener;
import com.ygo.game.listeners.SetButtonListener;
import com.ygo.game.utils.Utils;

import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteDataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ygo.game.YGO.debug;
import static com.ygo.game.YGO.info;

/**
 * Created by semahbod on 10/8/16.
 */
public class PlayState extends GameState implements InputProcessor {

    private static final int MAXIMUM_NORMAL_SUMMONS = 1;
    private static final String TAG = "Duel";
    public static final float PHASE_CHANGE_DELAY = 0.2f;

    private enum Intent {NONE, ATTACKING}

    public OrthographicCamera camera;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    DecalBatch decalBatch;
    public Field field;
    Map<Player, Hand> hands = new HashMap<>();
    TextFlash phaseChangeTextFlash;
    Map<Player, TextFlash> damageTextFlashes;

    Vector2 mouseDown = new Vector2();
    boolean mouseClicked = false;
    Skin skin;
    Stage stage;
    Table buttonTable, phaseTable;
    TextButton btnActivate, btnNormalSummon, btnSpecialSummon, btnSet, btnAttack, btnChangePosition,
            btnDrawPhase, btnStandbyPhase, btnMainPhase1, btnBattlePhase, btnMainPhase2, btnEndPhase;
    Array<TextButton> phaseButtons = new Array<TextButton>();
    Array<TargetingCursor> targetingCursors = new Array<TargetingCursor>();
    Array<Explosion> explosions = new Array<Explosion>();
    List<TargetingCursor> activationIndicators = new ArrayList<>();
    Map<Player, Lifepoints> lifepointBars = new HashMap<>();
    Map<Card, Set<Effect>> registeredEffects = new HashMap<>();
    /** Card ID -> Effect ID -> Effect */
    Map<UUID, Set<Effect>> activeEffects = new HashMap<>();
    Map<Player, Set<Buff>> playerBuffs = new HashMap<>();
    Cannonball cannonball;
    Map<Integer, AttackSwordVisual> attackSwordVisuals = new HashMap<>();
    Card currentlySelectedCard;
    Cell currentlySelectedCell;
    public Player turnPlayer = Player.PLAYER_1;
    public Player playerId;
    Phase currentPhase;
    Server server;
    Client client;
    boolean isServer;
    Dialog responseDialog;
    /** How many normal summons has the player conducted this turn? */
    int normalSummonsThisTurn;
    Intent intent;
    boolean attackTakingPlace;
    CardDao dao;
    /** Will be set to the card that triggered a card response */
    Card offendingCard;

    public PlayState(Server server, ServerListener serverListener, Client client) {
        this.server = server;
        this.client = client;
        this.isServer = server != null;

        if (isServer) {
            serverListener.playState = this;
            registerMessages(server);
            Gdx.graphics.setTitle("YGO: Player 1");
            playerId = Player.PLAYER_1;
        }
        else {
            Gdx.graphics.setTitle("YGO: Player 2");
            playerId = Player.PLAYER_2;
        }
        client.addListener(new ClientListener(this));
        registerMessages(client);

        YGO.debug(playerId + "'s client ID is " + client.getID());

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
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);

        field = new Field(this, 0.291f, 0.5f, playerId);

        decalBatch = new DecalBatch(new CameraGroupStrategy(Field.perspectiveCamera));

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas("ui/uiskin.atlas"));
        stage = new Stage(new StretchViewport(YGO.GAME_WIDTH, YGO.GAME_HEIGHT, camera));

        initCardMenus();

        hands.put(Player.PLAYER_1, new Hand(this, 0.625f, Player.PLAYER_1));
        hands.put(Player.PLAYER_2, new Hand(this, 0.625f, Player.PLAYER_2));

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
        damageTextFlashes = new HashMap<>();
        TextFlash dtf1 = new TextFlash(camera);
        dtf1.setPosition(camera.viewportWidth / 2 + 100, camera.viewportHeight / 2 - 500);
        dtf1.setColor(1, 0, 0, 1);
        damageTextFlashes.put(playerId, dtf1);
        TextFlash dtf2 = new TextFlash(camera);
        dtf2.setPosition(camera.viewportWidth / 2 + 100, camera.viewportHeight / 2 - 250);
        dtf2.setColor(1, 0, 0, 1);
        damageTextFlashes.put(playerId.getOpponent(), dtf2);

        lifepointBars.put(playerId, new Lifepoints(425, 670, 300, 35, 8000, playerId.toString()));
        lifepointBars.put(playerId.getOpponent(), new Lifepoints(825, 670, 300, 40, 8000, playerId.getOpponent().toString()));

        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl("jdbc:sqlite:db/ygo.db");
        DBI dbi = new DBI(ds);
        dao = dbi.onDemand(CardDao.class);

        // Prep the response dialog
        responseDialog = new Dialog("Response", skin) {
            @Override
            protected void result(Object object) {
                ResponseDialog answer = (ResponseDialog) object;
                switch (answer) {
                    case Accept:
                        //do nothing
                        break;
                    case Decline:
                        activationIndicators.clear();
                        //TODO: Resume gameplay
                        break;
                }
            }
        };
        TextButton yes = new TextButton("Yes", skin);
        yes.getLabel().setFontScale(1.2f);
        yes.setWidth(100);
        yes.setHeight(30);
        TextButton no = new TextButton("No", skin);
        no.getLabel().setFontScale(1.2f);
        no.setWidth(100);
        no.setHeight(30);
        responseDialog.text("Something happened. Activate a card?");
        responseDialog.button(yes, ResponseDialog.Accept);
        responseDialog.button(no, ResponseDialog.Decline);
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

        btnChangePosition = new TextButton("Change Position", skin);
        btnChangePosition.addListener(new ChangePositionListener(this));

        buttonTable.setVisible(false);

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

        // have the attack buttons drawn on top of the phase butons
        stage.addActor(buttonTable);
    }

    private void setupPhaseButtonListener(TextButton button, final Phase newPhase) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                client.sendTCP(new PhaseChangeMessage(newPhase));

                //TODO: Move this to the handleMessage() methods
                if (newPhase == Phase.MAIN_PHASE_2 || newPhase == Phase.END_PHASE) {
                    attackSwordVisuals.clear();
                }
            }
        });
    }

    private void initGame() {
        if (!isServer)
            return;

        List<Pair<UUID, String>> p1Deck = Arrays.asList(Gdx.files.internal("decks/player1.txt").readString().split("\\r?\\n")).stream()
                .map(name -> new Pair<>(UUID.randomUUID(), name)).collect(Collectors.toList());
        List<Pair<UUID, String>> p2Deck = Arrays.asList(Gdx.files.internal("decks/player2.txt").readString().split("\\r?\\n")).stream()
                .map(name -> new Pair<>(UUID.randomUUID(), name)).collect(Collectors.toList());

        server.sendToAllTCP(new GameInitializationMessage(p1Deck, p2Deck));

        float delay = 0.2f; // 1
        float perCardDelay = 0.1f; // 0.5f
        int cardsToDraw = 5;
        for (int i = 0; i < cardsToDraw; i++) {
            DelayedEvents.schedule(new Runnable() {
                @Override
                public void run() {
                    server.sendToAllTCP(new DrawMessage(Player.PLAYER_1));
                    server.sendToAllTCP(new DrawMessage(Player.PLAYER_2));
                }
            }, delay + i * perCardDelay);
        }

        DelayedEvents.schedule(new Runnable() {
            @Override
            public void run() {
                server.sendToAllTCP(new NextPlayersTurnMessage(Player.PLAYER_1));
            }
        }, delay + cardsToDraw * perCardDelay);

        DelayedEvents.schedule(new Runnable() {
            @Override
            public void run() {
                server.sendToAllTCP(new PhaseChangeMessage(Phase.DRAW_PHASE));
            }
        }, delay + cardsToDraw * perCardDelay + delay);

    }

    Vector2 loc = new Vector2();
    Vector2 loc2 = new Vector2();

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            client.sendTCP(new TestMessage(playerId.toString() + " sends TestMessage!"));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            destroyCardWithAnimation(field.getCellByIndex(playerId, Zone.FieldSpell, 0), playerId, 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            YGO.debug("Sending fake draw message for " + playerId.toString() + " on thread " + Thread.currentThread().getName());
            client.sendTCP(new DrawMessage(playerId));
//            server.sendToAllTCP(new DrawMessage(playerId));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
//            phaseChangeTextFlash.flash("Draw Phase", 0.33f, 0.67f);
//            debug("Flashing message");
            damageTextFlashes.get(playerId).flash("+1000", TextFlash.HEALING, 1f/3, 2f/3);
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && playerId == Player.PLAYER_1) {
            initCommon();
            initGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            debug("Mouse pos: " + Utils.getMousePos(camera));
        }

        //-----------------Debug stuff above

        //Map cardId -> Map<effectId, effect>
        activeEffects.forEach((cid, effects) -> {
            effects.forEach(e -> {
                CardManager.getCards().forEach(card -> {
                    // Apply the effect to cards that aren't already affected but satisfy the filter
                    if (!card.isAffectedByBuff(cid, e.getId()) && e.satisfiesFilter(card)) {
                        card.addBuff(cid, e.getId(), e.getBuff());
                        Gdx.app.log(TAG, "Applying effect (" + e.getType() + ") from " + CardManager.getUnique(cid).getName() + " to " + card.getName());
                    }

                    // Remove the effect from cards that had it, but no longer satisfy the filter
                    if (card.isAffectedByBuff(cid, e.getId()) && !e.satisfiesFilter(card)) {
                        card.removeBuff(cid, e.getId());
                        Gdx.app.log(TAG, "Removing effect (" + e.getType() + ") applied by " + CardManager.getUnique(cid).getName() + " from " + card.getName());
                    }
                });
            });
        });

        Tests.input(dt);
        DelayedEvents.update(dt);
        TweenAnimations.update(dt);
        for (TargetingCursor tc : targetingCursors) {
            tc.update(dt);
        }
        for (AttackSwordVisual asv : attackSwordVisuals.values()) {
            asv.update(dt);
        }
        if (!hands.get(Player.PLAYER_1).handleInput(dt, playerId) && !hands.get(Player.PLAYER_2).handleInput(dt, playerId) && !field.highlightCells() && clicked()) {
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
        damageTextFlashes.values().forEach(dtf -> dtf.update(dt));
        activationIndicators.forEach(i -> i.update(dt));
        if (cannonball != null) {
            cannonball.update(dt);
            if (cannonball.done) {
                if (cannonball.initiatedBy == playerId) {
                    if (cannonball.target != null) {
                        sendAttackMessage(cannonball.attacker, cannonball.target);
                    }
                    // direct attack
                    else {
                        sendDirectAttackMessage(cannonball.attacker);
                    }
                }
                cannonball = null;
            }
        }
    }

    @Override
    public void render() {
        field.renderGrid();
        field.renderCards(playerId, null);
        batch.begin();
        field.renderStats(playerId, batch);
        hands.values().forEach(h -> h.draw(batch, playerId));
        for (TargetingCursor tc : targetingCursors) {
            tc.render(batch);
        }
        for (Explosion e : explosions) {
            e.render(batch);
        }
        for (AttackSwordVisual asv : attackSwordVisuals.values()) {
            asv.render(batch);
        }
        activationIndicators.forEach(i -> i.render(batch));
        batch.end();
        lifepointBars.values().forEach(x -> x.render(batch, shapeRenderer));

        stage.draw();

        if (cannonball != null) {
            cannonball.render(decalBatch);
        }
        Utils.prepareViewport();
        decalBatch.flush();
        Utils.revertViewport();

        phaseChangeTextFlash.render();
        damageTextFlashes.values().forEach(TextFlash::render);

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

    private boolean hasMonsters(Player player) {
        for (Cell c : field.getZone(Zone.Monster, player)) {
            if (c.hasCard()) {
                return true;
            }
        }
        return false;
    }

    private boolean canNormalSummon() {
        return normalSummonsThisTurn < MAXIMUM_NORMAL_SUMMONS;
    }

    public void showCardMenu(Card card) {
        if (turnPlayer != playerId || (currentPhase != Phase.MAIN_PHASE_1 && currentPhase != Phase.MAIN_PHASE_2)) {
            return;
        }

        currentlySelectedCard = card;
        buttonTable.clear();
        buttonTable.setVisible(true);
        buttonTable.setPosition(Utils.getMousePos(camera).x + 40, Utils.getMousePos(camera).y + 50);
        if (card.getType() == CardType.Monster) {
            if (canNormalSummon()) {
                addButtonToTable(btnNormalSummon);
                addButtonToTable(btnSet);
            }
        }
        else if (card.getType() == CardType.Spell) {
            addButtonToTable(btnActivate);
            addButtonToTable(btnSet);
        }
        else if (card.getType() == CardType.Trap) {
            addButtonToTable(btnSet);
        }
    }

    public void showFieldCardMenu(Card card, Cell cell) {
        currentlySelectedCard = card;
        currentlySelectedCell = cell;
        buttonTable.clear();
        buttonTable.setVisible(true);
        buttonTable.setPosition(Utils.getMousePos(camera).x + 40, Utils.getMousePos(camera).y + 50);
        if (card.getType() == CardType.Monster) {
            if (currentPhase == Phase.BATTLE_PHASE && card.canAttack()) {
                buttonTable.add(btnAttack).width(100);
            }
            if (card.canChangeBattlePosition() && (currentPhase == Phase.MAIN_PHASE_1 || currentPhase == Phase.MAIN_PHASE_2)) {
                buttonTable.add(btnChangePosition).width(100);
            }
        }

        Optional<TargetingCursor> c = activationIndicators.stream().filter(i -> i.getCell() == cell).findFirst();
        c.ifPresent(i -> buttonTable.add(btnActivate));
    }

    public void hideCardMenu() {
        buttonTable.setVisible(false);
    }

    public void performNormalSummon() {
        performSummon(SummonType.NormalSummon, new CardPlayMode(CardPlayMode.FACE_UP | CardPlayMode.ATTACK_MODE));
        normalSummonsThisTurn++;
    }

    public void performEffectActivation() {
        activationIndicators.clear();
        if (offendingCard != null) {
            client.sendTCP(new CardActivationMessage(playerId, offendingCard, currentlySelectedCard));
        }
        else {
            client.sendTCP(new CardActivationMessage(playerId, currentlySelectedCard));
        }
    }

    //TODO: This will later take a parameter indicating where the card is being set from (hand, deck, graveyard...)
    public void performSet() {
        if (currentlySelectedCard.getType() == CardType.Monster) {
            performSummon(SummonType.Set, new CardPlayMode(CardPlayMode.FACE_DOWN | CardPlayMode.DEFENSE_MODE));
        }
        else {
            SpellTrapSetMessage m = new SpellTrapSetMessage(playerId.index, Location.Hand.index, currentlySelectedCard.getId());
            client.sendTCP(m);
        }
    }

    private void performSummon(SummonType summonType, CardPlayMode cardPlayMode) {
//        Hand hand = hands[turnPlayer.index];
//        if (currentlySelectedCard.location == Location.Hand) {
//            hand.removeCard(currentlySelectedCard, turnPlayer);
//        }
//        field.placeCardOnField(currentlySelectedCard, Zone.Monster, turnPlayer, cardPlayMode, Location.Field);

        //TODO: pass location as parameter
        SummonMessage m = new SummonMessage(playerId, Location.Hand, currentlySelectedCard.getId(), summonType, cardPlayMode);
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

        if (button == Input.Buttons.RIGHT) {
            if (intent == Intent.ATTACKING) {
                targetingCursors.clear();
            }
            if (!activationIndicators.isEmpty()) {
                responseDialog.hide();
                activationIndicators.clear();
                //TODO: Resume gameplay
            }

            intent = Intent.NONE;
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
        k.register(AttackInitiationMessage.class);
        k.register(RetaliatoryDamageMessage.class);
        k.register(UUID.class, new JavaSerializer());
        k.register(TestMessage.class);
        k.register(Pair.class);
        k.register(ArrayList.class);
        k.register(DirectAttackMessage.class);
        k.register(DirectAttackInitiationMessage.class);
        k.register(CardActivationMessage.class);
        k.register(BattlePositionChangeMessage.class);
    }

    private void drawCard(Player player) {
        Card card = field.removeCard(player, Zone.Deck, Field.TOP_CARD);
        hands.get(player).addCard(card, playerId);
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
        List<Card> p1Deck, p2Deck;

        p1Deck = m.p1Deck.stream().map(pair -> new Card(dao.getCardByName(pair.second), pair.first, Player.PLAYER_1)).collect(Collectors.toList());
        p2Deck = m.p2Deck.stream().map(pair -> new Card(dao.getCardByName(pair.second), pair.first, Player.PLAYER_2)).collect(Collectors.toList());

        CardManager.submitCardsForPlay(p1Deck);
        CardManager.submitCardsForPlay(p2Deck);

        field.placeCardsInZone(p1Deck, Zone.Deck, Player.PLAYER_1, new CardPlayMode(CardPlayMode.FACE_DOWN), Location.Deck);
        field.placeCardsInZone(p2Deck, Zone.Deck, Player.PLAYER_2, new CardPlayMode(CardPlayMode.FACE_DOWN), Location.Deck);

        for (Player p : Player.values()) {
            for (Zone z : Zone.values()) {
                for (Cell c : field.getZone(z, p)) {
                    c.card = null;
                }
            }
        }

//        field.placeCardOnField(new Card(dao.getCardByName("Maha Vailo"), Player.PLAYER_1), Zone.Monster, Player.PLAYER_1, CardPlayMode.FACE_UP_ATTACK, Location.Field);
//        field.placeCardOnField(new Card(dao.getCardByName("Baby Dragon"), Player.PLAYER_1), Zone.Monster, Player.PLAYER_2, CardPlayMode.FACE_UP_ATTACK, Location.Field);
//        field.placeCardOnField(new Card(dao.getCardByName("Mystical Elf"), Player.PLAYER_2), Zone.Monster, Player.PLAYER_2, CardPlayMode.FACE_UP_ATTACK, Location.Field);

        //redundant
        turnPlayer = Player.PLAYER_1;

        // Set up lua functions
        CardManager.initializeLuaScripts(this, CardManager.getUniqueCardsInPlay());

        CardManager.getUniqueCardsInPlay().forEach(card -> {
            if (!registeredEffects.containsKey(card)) {
                registeredEffects.put(card, new HashSet<>());
            }
        });

        CardManager.getCards().forEach(card -> {
            activeEffects.put(card.getId(), new HashSet<>());
        });
    }

    public void handleDrawMessage(DrawMessage m) {
        drawCard(Player.valueOf(m.player));
    }

    public void handleSummonMessage(SummonMessage m) {
        Player summoner = Player.valueOf(m.player);
        SummonType summonType = SummonType.valueOf(m.summonType);
        Location location = Location.valueOf(m.location);
        Card card = CardManager.getUnique(m.cardId);
        YGO.debug("Summoning " + card.getName() + " (" + card.getId() + ") for " + summoner);
        if (location == Location.Hand) {
            hands.get(summoner).removeCard(card, playerId);
        }
        if (summonType == SummonType.NormalSummon || summonType == SummonType.Set) {
            card.normalSummonedOrSetThisTurn = true;
        }
        field.placeCardOnField(card, Zone.Monster, summoner, new CardPlayMode(m.cardPlayMode), Location.Field);

        // Add any continuous effects this card has (e.g. Milus Radiant)
        registeredEffects.get(card).forEach(e -> {
            if (e.getFlags().contains(Effect.Flag.Continuous)) {
                activeEffects.get(card.getId()).add(e);
                Gdx.app.log(TAG, "Instating continuous effect (" + e.getType() + ") " + e.getId() + " from " + card.getName());
            }
        });

        // Check if there are any card effects listening for a monster being summoned
        //TODO: Finish Trap Hole
        if (summonType == SummonType.NormalSummon /** or flip summon */) {
            CardManager.getCards().stream()
                    .filter(c -> c.getController() == playerId && c.getLocation() == Location.Field)
                    .filter(c -> c.getType() == CardType.Trap && !c.spellTrapSetThisTurn)
                    .forEach(c -> {
                        Set<Effect> effects = registeredEffects.get(c);
                        Optional<Effect> effect = effects.stream()
                                .filter(e -> e.getResponseCriteria().contains(Effect.Response.OnNormalSummon))
                                .filter(e -> e.getFilter().call(CoerceJavaToLua.coerce(card), CoerceJavaToLua.coerce(playerId)).toboolean())
                                .findFirst();
                        effect.ifPresent(e -> activationIndicators.add(new TargetingCursor(field.getCellContainingCard(c), YGO.cardActivationCursor)));
                    });
        }

        if (!activationIndicators.isEmpty()) {
            responseDialog.show(stage);
            offendingCard = card;
        }
    }

    public void handleSpellTrapSetMessage(SpellTrapSetMessage m) {
        Player player = Player.indexToPlayer(m.player);
        Card card = CardManager.getUnique(m.cardId);
        if (Location.indexToLocation(m.location) == Location.Hand) {
            hands.get(player).removeCard(card, playerId);
        }
        Zone destination = card.isFieldSpell() ? Zone.FieldSpell : Zone.SpellTrap;
        field.placeCardOnField(card, destination, player, new CardPlayMode(CardPlayMode.FACE_DOWN), Location.Field);
        card.spellTrapSetThisTurn = true;
        Gdx.app.log(TAG, "Set " + card.nameId());
    }

    public void handlePhaseChangeMessage(PhaseChangeMessage m) {
        YGO.debug("Phase is now: " + m.newPhase);
        currentPhase = Phase.valueOf(m.newPhase);
        phaseChangeTextFlash.flash(currentPhase.toString(), 1f / 3f, 2f / 3f);
        if (playerId == turnPlayer) {
            if (currentPhase == Phase.DRAW_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnDrawPhase);
                // draw
                DelayedEvents.schedule(() -> client.sendTCP(new DrawMessage(playerId)), PHASE_CHANGE_DELAY / 2);
                //TODO have to check if there are no events that occur during draw phase
                advanceToPhase(Phase.STANDBY_PHASE, PHASE_CHANGE_DELAY);
            }
            else if (currentPhase == Phase.STANDBY_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnStandbyPhase);
                //TODO have to check if there are no events that occur during standby phase
                advanceToPhase(Phase.MAIN_PHASE_1, PHASE_CHANGE_DELAY);
            }
            else if (currentPhase == Phase.MAIN_PHASE_1) {
                setPhaseButtonVisibleButOthersNot(btnMainPhase1);
                showPhaseButton(btnBattlePhase, btnEndPhase);
            }
            else if (currentPhase == Phase.BATTLE_PHASE) {
                setPhaseButtonVisibleButOthersNot(btnBattlePhase);
                showPhaseButton(btnMainPhase2, btnEndPhase);
                showAttackSwordVisualsForEligibleMonsters();
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

    private void showAttackSwordVisualsForEligibleMonsters() {
        field.getZone(Zone.Monster, playerId).stream().filter(c -> c.hasCard() && c.card.canAttack()).forEach(c -> {
            attackSwordVisuals.put(c.index, new AttackSwordVisual(c));
        });
    }

    public void handleNextPlayersTurnMessage(NextPlayersTurnMessage m) {
        turnPlayer = Player.valueOf(m.player);
        phaseChangeTextFlash.flash(turnPlayer.toString() + "'s Turn", 1f / 3f, 2f / 3f);
//        if (playerId == turnPlayer) {
            normalSummonsThisTurn = 0;
            field.getZone(Zone.Monster, playerId).stream().filter(Cell::hasCard).forEach(c -> {
                c.card.attacksThisTurn = 0;
                c.card.normalSummonedOrSetThisTurn = false;
            });
            field.getZone(Zone.SpellTrap, playerId).stream().filter(Cell::hasCard).forEach(c -> {
                c.card.spellTrapSetThisTurn = false;
            });
//        }
    }

    public void handleAttackInitiationMessage(AttackInitiationMessage m) {
        cannonball = new Cannonball(field, m);
    }

    /**
     * Received by the person getting attacked
     * @param m
     */
    public void handleAttackMessage(AttackMessage m) {
        final Player conducting = Player.valueOf(m.player);
        List<Cell> cells = field.getZone(Zone.Monster, conducting.getOpponent());
        int index = m.targetCell;
        if (conducting == playerId) {
            intent = Intent.NONE;
        }
        final Cell defendingCell = cells.get(index);
        final Cell attackingCell = field.getCellByIndex(conducting, Zone.Monster, m.sourceCell);
        int incomingAtkPower = attackingCell.card.getAtk();
        // Check if our monster is in defense mode
        if (defendingCell.card.getPlayMode().isDefenseMode()) {
            int defensePower = defendingCell.card.getDef();
            if (defendingCell.card.getPlayMode().isFaceDown()) {
                //TODO: Apply flip effects here
                defendingCell.card.setPlayMode(CardPlayMode.FACE_UP);
            }

            if (incomingAtkPower > defensePower) {
                destroyCardWithAnimation(defendingCell, conducting.getOpponent(), 1);
                //TODO: This is where piercing damage would apply
            }
            else if (incomingAtkPower < defensePower) {
                inflictRetaliatoryDamage(conducting, attackingCell, defendingCell, defensePower - incomingAtkPower);
            }
        }
        else {
            int defenderAtkPower = defendingCell.card.getAtk();
            if (incomingAtkPower >= defenderAtkPower) {
                int damageDifference = incomingAtkPower - defenderAtkPower;
                if (damageDifference > 0) {
                    inflictDamage(conducting.getOpponent(), DamageType.BATTLE, damageDifference);
                }
                destroyCardWithAnimation(defendingCell, conducting.getOpponent(), 1);
                if (incomingAtkPower == defenderAtkPower) {
                    inflictRetaliatoryDamage(conducting, attackingCell, defendingCell, 0);
                }
            }
            else {
                inflictRetaliatoryDamage(conducting, attackingCell, defendingCell, defenderAtkPower - incomingAtkPower);
            }
        }


//        AttackData.attackTakingPlace = true;
//        AttackData.attacker = conducting;
//        AttackData.cellOrigin =

    }

    public void handleRetaliatoryDamageMessage(RetaliatoryDamageMessage m) {
        Player attacker = Player.valueOf(m.victimPlayer);
        Player defender = attacker.getOpponent();
        Cell attackingCell = field.getCellByIndex(attacker, Zone.Monster, m.attackingCellIndex);
        Cell defendingCell = field.getCellByIndex(defender, Zone.Monster, m.defendingCellIndex);
        inflictDamage(attacker, DamageType.BATTLE, m.damage);

        if (attackingCell.card.getPlayMode().isAttackMode() && defendingCell.card.getPlayMode().isAttackMode()) {
            YGO.debug("Retaliatory damage message");
            destroyCardWithAnimation(attackingCell, attacker, 1);
        }
    }

    public void handleDirectAttackInitiationMessage(DirectAttackInitiationMessage m) {
        cannonball = new Cannonball(field, m);
    }

    public void handleDirectAttackMessage(DirectAttackMessage m) {
        final Player conducting = Player.valueOf(m.player);
        if (conducting == playerId) {
            intent = Intent.NONE;
        }
        final Cell attackingCell = field.getCellByIndex(conducting, Zone.Monster, m.sourceCell);
        int incomingAtkPower = attackingCell.card.getAtk();

        inflictDamage(conducting.getOpponent(), DamageType.BATTLE, incomingAtkPower);
    }

    public void handleCardActivationMessage(CardActivationMessage m) {
        //Naive implementation for now
        Player activator = Player.valueOf(m.activator);
        Card card = CardManager.getUnique(m.cardId);
        YGO.debug("Activating " + card.getName() + " (" + card.getId() + ") for " + activator);
        Cell destinationCell = null;
        if (card.getLocation() == Location.Hand) {
            hands.get(activator).removeCard(card, playerId);
            Zone zone = Zone.SpellTrap;
            if (card.isFieldSpell()) {
                zone = Zone.FieldSpell;
            }

            // Place the card on the field if it was in the hand
            destinationCell = field.placeCardOnField(card, zone, activator, new CardPlayMode(CardPlayMode.FACE_UP), Location.Field);
        }
        else if (card.getLocation() == Location.Field) {
            if (card.getPlayMode().isFaceDown()) {
                card.setPlayMode(CardPlayMode.FACE_UP);
            }
            destinationCell = field.getCellContainingCard(card);
        }


        boolean hasContinuousEffect = false;
        //TODO: Got rid of effect null check. Bad idea?
        for (Effect e : registeredEffects.get(card)) {
            if (e.getActivationCriteria().contains(Effect.Criteria.OnActivation)) {
                if (e.getFlags().contains(Effect.Flag.Continuous)) {
                    activeEffects.get(card.getId()).add(e);
                    hasContinuousEffect = true;
                }
                else {
                    if (m.offendingCard != null) {
                        // Was triggered by something another card did
                        e.getOperation().call(CoerceJavaToLua.coerce(CardManager.getUnique(m.offendingCard)));
                    }
                    else {
                        e.getOperation().call(CoerceJavaToLua.coerce(activator));
                    }
                }
            }
        }

        final Cell lambdaDestinationCell = destinationCell;
        if (!hasContinuousEffect && card.getType() != CardType.Monster) {
            DelayedEvents.schedule(() -> {
                TweenAnimations.submit(card, lambdaDestinationCell, field.getZone(Zone.Graveyard, activator).get(0), () -> {
                    card.location = Location.Graveyard;
                    lambdaDestinationCell.card = null;
                    MultiCardCell mcc = (MultiCardCell) field.getZone(Zone.Graveyard, activator).get(0);
                    mcc.cards.add(card);
                });
            }, 1f);
        }
    }

    public void handleBattlePositionChangeMessage(BattlePositionChangeMessage m) {
        Card card = CardManager.getUnique(m.cardId);
        card.overwritePlayMode(new CardPlayMode(m.battlePosition));
        card.markBattlePositionChanged();
    }

    private void inflictRetaliatoryDamage(Player victim, Cell attackingCell, Cell defendingCell, int damage) {
//        client.sendTCP(new RetaliatoryDamageMessage(victim, attackingCell, defendingCell, damage));
        handleRetaliatoryDamageMessage(new RetaliatoryDamageMessage(victim, attackingCell, defendingCell, damage));
    }

    public void destroyCard(Card card) {
        destroyCardWithAnimation(field.getCellContainingCard(card), card.getOwner(), 1);
    }

    private void destroyCardWithAnimation(Cell cell, Player owner, float delay) {
        explosions.add(new Explosion(cell));
        DelayedEvents.schedule(() -> {
            Gdx.app.log(TAG, String.format("Destroying %s", cell.card.getName()));
            Card card = cell.card;
            card.location = Location.Graveyard;
            card.setZone(Zone.Graveyard);
            card.overwritePlayMode(CardPlayMode.FACE_UP_ATTACK);
            MultiCardCell mc = (MultiCardCell) field.getZone(Zone.Graveyard, owner).get(0);
            mc.cards.add(cell.card);
            cell.card = null;

            // Stop the destroyed card's effect, if it had one
            List<Effect> effectsToRemove = new ArrayList<>();
            activeEffects.get(card.getId()).forEach(effect -> {
                // Continuous effects should be canceled once the card is destroyed
                if (effect.getFlags().contains(Effect.Flag.Continuous)) {
                    removeBuffsFromCards(card.getId(), effect.getId());
                    effectsToRemove.add(effect);
                }
            });

            // Delete the effects from the activeEffects map
            effectsToRemove.forEach(e -> {
                activeEffects.get(card.getId()).remove(e);
                Gdx.app.log(TAG, "Removing effect " + e.getId() + " applied by " + card.getName());
            });

            TweenAnimations.submit(card, cell, field.getZone(Zone.Graveyard, owner).get(0), () -> {
                card.location = Location.Graveyard;
                cell.card = null;
                MultiCardCell mcc = (MultiCardCell) field.getZone(Zone.Graveyard, owner).get(0);
                mcc.cards.add(card);
            });
        }, delay);
    }

    private void removeBuffsFromCards(UUID cardId, UUID effectId) {
        CardManager.getCards().stream().filter(c -> c.isAffectedByBuff(cardId, effectId)).forEach(c -> {
            c.removeBuff(cardId, effectId);
        });
    }

    public void inflictDamage(Player target, DamageType damageType, int amount) {
        lifepointBars.get(target).currentLifePoints = Math.max(0, lifepointBars.get(target).currentLifePoints - amount);
        if (amount > 0) {
            damageTextFlashes.get(target).flash("-" + amount, TextFlash.DAMAGE, 1f / 3, 2f / 3);
        }
        //deal with damageType as the need arises

        if (lifepointBars.get(target).currentLifePoints == 0) {
            YGO.info(target.getOpponent().toString() + " wins!");
            // TODO: Win condition
        }
    }

    public void increaseLifepoints(Player target, int amount) {
        lifepointBars.get(target).currentLifePoints += amount;
        damageTextFlashes.get(target).flash("+" + amount, TextFlash.HEALING, 1f / 3, 2f / 3);
    }

    public void displayAttackTargets() {
        // Direct attack
        if (!hasMonsters(playerId.getOpponent())) {
            client.sendTCP(new DirectAttackInitiationMessage(playerId, currentlySelectedCell.index));
            currentlySelectedCard.attacksThisTurn++;
            if (!currentlySelectedCard.canAttack()) {
                attackSwordVisuals.remove(currentlySelectedCell.index);
            }
        }
        else {
            intent = Intent.ATTACKING;
            targetingCursors.clear();
            field.getZone(Zone.Monster, playerId.getOpponent()).stream().filter(Cell::hasCard).forEach(c -> {
                targetingCursors.add(new TargetingCursor(c, YGO.targetingCursor));
                c.targetingCursorOn = true;
            });
        }
    }

    /**
     * Where the attack is confirmed, send an attack message
     * @param target
     */
    public void confirmTarget(Cell target) {
        if (intent == Intent.NONE) {
            return;
        }

        //TODO: NEED TO INDICATE THE CELL THAT WE ARE ATTACKING FROM
        if (intent == Intent.ATTACKING) {
            client.sendTCP(new AttackInitiationMessage(playerId, currentlySelectedCell.index, target.index));
            clearAllTargeting();
            currentlySelectedCard.attacksThisTurn++;
            if (!currentlySelectedCard.canAttack()) {
                attackSwordVisuals.remove(currentlySelectedCell.index);
            }
        }
    }

    public void sendAttackMessage(Cell attacker, Cell target) {
        client.sendTCP(new AttackMessage(playerId, attacker, target));
    }

    public void sendDirectAttackMessage(Cell attacker) {
        client.sendTCP(new DirectAttackMessage(playerId, attacker));
    }

    private void clearAllTargeting() {
        targetingCursors.clear();
        field.clearTargeting();
    }

    public void changeBattlePosition() {
        client.sendTCP(new BattlePositionChangeMessage(currentlySelectedCard, currentlySelectedCard.getPlayMode().getOpposite()));
    }

    public void registerEffect(Effect effect, Card card) {
        if (!registeredEffects.containsKey(card)) {
            registeredEffects.put(card, new HashSet<>());
        }
        registeredEffects.get(card).add(effect);
        Gdx.app.log(TAG, "Registered effect with type " + effect.getType() + " for " + card.getName());
    }

    public void applyBuff(Player target, Buff.Type type, Phase expiration, int passNumber) {

    }
}
