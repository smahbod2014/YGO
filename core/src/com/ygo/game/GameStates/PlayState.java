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
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;
import com.ygo.game.Card;
import com.ygo.game.CardManager;
import com.ygo.game.Field;
import com.ygo.game.Hand;
import com.ygo.game.Messages.DrawMessage;
import com.ygo.game.Messages.GameInitializationMessage;
import com.ygo.game.Messages.SpellTrapSetMessage;
import com.ygo.game.Messages.SummonMessage;
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
    Table buttonTable;
    TextButton btnActivate, btnNormalSummon, btnSpecialSummon, btnSet;
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
        buttonTable = new Table();
        btnActivate = new TextButton("Activate", skin);
        btnActivate.addListener(new ActivateButtonListener(this));

        btnNormalSummon = new TextButton("Normal Summon", skin);
        btnNormalSummon.addListener(new NormalSummonButtonListener(this));

        btnSet = new TextButton("Set", skin);
        btnSet.addListener(new SetButtonListener(this));

        buttonTable.setVisible(false);

        stage.addActor(buttonTable);
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
        field.highlightCells();
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

    private void addButtonToTable(TextButton button) {
        buttonTable.add(button).width(140).fill().row();
    }

    public void showCardMenu(Card card) {
        currentlySelectedCard = card;
        buttonTable.clear();
        buttonTable.setVisible(true);
        buttonTable.setPosition(Utils.getMousePos(camera).x + 40, Utils.getMousePos(camera).y + 50);
        if (card.isMonster()) {
            addButtonToTable(btnNormalSummon);
            addButtonToTable(btnSet);
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

    public void hideCardMenu() {
        buttonTable.setVisible(false);
    }

    public void performNormalSummon() {
        performSummon(SummonType.NORMAL_SUMMON, CardPlayMode.FACE_UP | CardPlayMode.ATTACK_MODE);
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
}
