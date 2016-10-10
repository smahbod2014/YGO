package com.ygo.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;
import com.ygo.game.Card;
import com.ygo.game.CardManager;
import com.ygo.game.Field;
import com.ygo.game.Hand;
import com.ygo.game.Messages.DrawMessage;
import com.ygo.game.Messages.GameInitializationMessage;
import com.ygo.game.ServerListener;
import com.ygo.game.Tests.Tests;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.SummonType;
import com.ygo.game.Types.ZoneType;
import com.ygo.game.Utils;
import com.ygo.game.YGO;
import com.ygo.game.listeners.ActivateButtonListener;
import com.ygo.game.listeners.NormalSummonButtonListener;
import com.ygo.game.listeners.SetButtonListener;

import static com.ygo.game.YGO.debug;
import static com.ygo.game.YGO.info;

/**
 * Created by semahbod on 10/8/16.
 */
public class PlayState extends GameState implements InputProcessor {

    public OrthographicCamera camera;
    SpriteBatch batch;
    public Field field;
    Hand[] hands = new Hand[2];

    Vector2 mouseDown = new Vector2();
    boolean mouseClicked = false;
    Skin skin;
    Stage stage;
    Table monsterTable;
    Card currentlySelectedCard;
    PlayerType turnPlayer = PlayerType.PLAYER_1;
    PlayerType playerId;
    Server server;
    Client client;
    boolean isServer;

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
    }

    public PlayState(Client client) {
        this(null, null, client);
    }

    private void initCommon() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, YGO.GAME_WIDTH, YGO.GAME_HEIGHT);
        batch.setProjectionMatrix(camera.combined);

        field = new Field(0.291f, 0.5f, playerId);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas("ui/uiskin.atlas"));
        stage = new Stage(new StretchViewport(YGO.GAME_WIDTH, YGO.GAME_HEIGHT, camera));

        initCardMenus();

        hands[0] = new Hand(this, 0.625f, PlayerType.PLAYER_1);
        hands[1] = new Hand(this, 0.625f, PlayerType.PLAYER_2);

        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initCardMenus() {
        monsterTable = new Table();
        TextButton activate = new TextButton("Activate", skin);
        activate.addListener(new ActivateButtonListener(this));

        TextButton ns = new TextButton("Normal Summon", skin);
        ns.addListener(new NormalSummonButtonListener(this));

        TextButton set = new TextButton("Set", skin);
        set.addListener(new SetButtonListener(this));

        monsterTable.add(activate).fill().row();
        monsterTable.add(ns).fill().row();
        monsterTable.add(set).fill().row();
        monsterTable.setVisible(false);

        stage.addActor(monsterTable);
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

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                server.sendToAllTCP(new DrawMessage(PlayerType.PLAYER_1.index));
                server.sendToAllTCP(new DrawMessage(PlayerType.PLAYER_2.index));
            }
        }, 1, 0.5f, 4);
    }

    @Override
    public void update(float dt) {
        Tests.input(dt);
        hands[0].handleInput(dt, playerId);
        hands[1].handleInput(dt, playerId);
        stage.act(dt);
    }

    @Override
    public void render() {
        field.renderGrid();
        field.renderCards(playerId);
        batch.begin();
        hands[0].draw(batch, playerId);
        hands[1].draw(batch, playerId);
        batch.end();

        stage.draw();

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

    public void showCardMenu(Card card) {
        currentlySelectedCard = card;
        if (card.isMonster()) {
            monsterTable.setVisible(true);
            monsterTable.setPosition(Utils.getMousePos(camera).x, Utils.getMousePos(camera).y);
            debug(monsterTable.isVisible() ? "Table should be visible" : "Table is NOT visible...");
            debug("Monster card clicked");
        }
        else if (card.isSpell()) {
            debug("Spell card clicked");
        }
        else if (card.isTrap()) {
            debug("Trap card clicked");
        }
        else {
            debug("ERROR: Unknown card type clicked");
        }
    }

    public void hideCardMenu() {
        monsterTable.setVisible(false);
    }

    public void performNormalSummon() {
        performSummon(SummonType.NORMAL_SUMMON, CardPlayMode.FACE_UP | CardPlayMode.ATTACK_MODE);
    }

    public void performEffectActivation() {
        info("performEffectActivation not implemented");
    }

    public void performSet() {
        performSummon(SummonType.SET, CardPlayMode.FACE_DOWN | CardPlayMode.DEFENSE_MODE);
    }

    private void performSummon(SummonType summonType, int cardPlayMode) {
        Hand hand = hands[turnPlayer.index];
        if (currentlySelectedCard.location == Location.HAND) {
            hand.removeCard(currentlySelectedCard, turnPlayer);
        }
        field.placeCardOnField(currentlySelectedCard, ZoneType.MONSTER, turnPlayer, cardPlayMode, Location.FIELD);
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
        ep.getKryo().register(Object[].class);
        ep.getKryo().register(Array.class);
        ep.getKryo().register(GameInitializationMessage.class);
        ep.getKryo().register(DrawMessage.class);
    }

    private void drawCard(PlayerType player) {
        Card card = field.removeCard(player, ZoneType.DECK, Field.TOP_CARD);
        hands[player.index].addCard(card, playerId);
        debug(player.toString() + " drew " + card.id);
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

        field.placeCardOnField(CardManager.get("93013676").copy(), ZoneType.MONSTER, PlayerType.PLAYER_1, CardPlayMode.FACE_UP, Location.FIELD);
        field.placeCardOnField(CardManager.get("88819587").copy(), ZoneType.FIELD_SPELL, PlayerType.PLAYER_2, CardPlayMode.FACE_UP, Location.FIELD);
    }

    public void handleDrawMessage(DrawMessage m) {
        drawCard(PlayerType.indexToPlayer(m.player));
    }
}
