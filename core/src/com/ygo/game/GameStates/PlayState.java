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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.ygo.game.Card;
import com.ygo.game.CardManager;
import com.ygo.game.Field;
import com.ygo.game.Hand;
import com.ygo.game.Tests.Tests;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.SummonType;
import com.ygo.game.Types.ZoneType;
import com.ygo.game.Utils;
import com.ygo.game.YGO;
import com.ygo.game.YGOServer;
import com.ygo.game.listeners.ActivateButtonListener;
import com.ygo.game.listeners.NormalSummonButtonListener;
import com.ygo.game.listeners.SetButtonListener;

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
    PlayerType turnPlayer = PlayerType.CURRENT_PLAYER;
    Server server;
    Client client;
    boolean ownsServer;

    public PlayState(Server server, Client client) {
        this.server = server;
        this.client = client;
        this.ownsServer = true;
        info("Server started complete");
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, YGO.GAME_WIDTH, YGO.GAME_HEIGHT);
        batch.setProjectionMatrix(camera.combined);

        field = new Field(0.291f, 0.5f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas("ui/uiskin.atlas"));
        stage = new Stage(new StretchViewport(YGO.GAME_WIDTH, YGO.GAME_HEIGHT, camera));

        initCardMenus();

        hands[0] = new Hand(this, 0.625f, PlayerType.CURRENT_PLAYER);
        hands[0].addCard(CardManager.get("3573512"));
        hands[0].addCard(CardManager.get("7489323"));
        hands[0].addCard(CardManager.get("80770678"));
        hands[0].addCard(CardManager.get("88819587"));
        hands[0].addCard(CardManager.get("93013676"));

        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public PlayState() {

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

    @Override
    public void update(float dt) {
        Tests.input(dt);
        hands[0].handleInput(dt);
        stage.act(dt);
    }

    @Override
    public void render() {
        field.renderGrid();
        field.renderCards();
        batch.begin();
        hands[0].draw(batch);
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
        if (ownsServer) {
            server.stop();
        }
        client.stop();
    }

    public void showCardMenu(Card card) {
        currentlySelectedCard = card;
        switch (card.cardType) {
            case MONSTER:
                monsterTable.setVisible(true);
                monsterTable.setPosition(Utils.getMousePos(camera).x, Utils.getMousePos(camera).y);
                break;
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
            hand.removeCard(currentlySelectedCard);
        }
        field.placeCardOnField(currentlySelectedCard, ZoneType.MONSTER, turnPlayer, cardPlayMode);
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
}
