package com.ygo.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.SummonType;
import com.ygo.game.Types.ZoneType;
import com.ygo.game.listeners.ActivateButtonListener;
import com.ygo.game.listeners.NormalSummonButtonListener;
import com.ygo.game.listeners.SetButtonListener;

public class YGO extends ApplicationAdapter implements InputProcessor {

	public static int WINDOW_WIDTH = 1500;
    public static int WINDOW_HEIGHT = WINDOW_WIDTH*9/16;

    static boolean isCardMenuShowing;
    static OrthographicCamera camera;
	SpriteBatch batch;
    static Field field;
    static Hand[] hands = new Hand[2];

    Vector2 mouseDown = new Vector2();
    static boolean mouseClicked = false;
    Skin skin;
    Stage stage;
    static Table monsterTable;
    static Card currentlySelectedCard;
    static PlayerType turnPlayer = PlayerType.CURRENT_PLAYER;
	
	@Override
	public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
        camera = new OrthographicCamera(WINDOW_WIDTH, WINDOW_HEIGHT);
        camera.translate(WINDOW_WIDTH/2, WINDOW_HEIGHT/2);
        camera.update();
//        camera = new OrthographicCamera();
//        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.setProjectionMatrix(camera.combined);
        field = new Field(0.291f, 0.5f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas("ui/uiskin.atlas"));
        stage = new Stage(new ScalingViewport(Scaling.fit, WINDOW_WIDTH, WINDOW_HEIGHT));
        initCardMenus();

        Card.FACE_DOWN_CARD = new Sprite(new Texture("cards/cover.jpg"));

        hands[0] = new Hand(0.75f, PlayerType.CURRENT_PLAYER);
        hands[0].addCard(new Card("3573512", CardType.MONSTER));
        hands[0].addCard(new Card("7489323", CardType.MONSTER));
        hands[0].addCard(new Card("80770678", CardType.MONSTER));
        hands[0].addCard(new Card("88819587", CardType.MONSTER));
        hands[0].addCard(new Card("93013676", CardType.MONSTER));

        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);
	}

    private void initCardMenus() {
        monsterTable = new Table();
        TextButton activate = new TextButton("Activate", skin);
        activate.setWidth(Utils.sx(100));
        activate.setHeight(Utils.sy(30));
        activate.getLabel().setFontScale(Utils.getCurrentWindowScaleX(), Utils.getCurrentWindowScaleY());
        activate.addListener(new ActivateButtonListener());

        TextButton ns = new TextButton("Normal Summon", skin);
        ns.setWidth(Utils.sx(100));
        ns.setHeight(Utils.sy(30));
        ns.getLabel().setFontScale(Utils.getCurrentWindowScaleX(), Utils.getCurrentWindowScaleY());
        ns.addListener(new NormalSummonButtonListener());

        TextButton set = new TextButton("Set", skin);
        set.setWidth(Utils.sx(100));
        set.setHeight(Utils.sy(30));
        set.getLabel().setFontScale(Utils.getCurrentWindowScaleX(), Utils.getCurrentWindowScaleY());
        set.addListener(new SetButtonListener());

        monsterTable.add(activate).fill().row();
        monsterTable.add(ns).fill().row();
        monsterTable.add(set).fill().row();
        monsterTable.setVisible(false);

        stage.addActor(monsterTable);
    }

    @Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float dt = Gdx.graphics.getDeltaTime();
        hands[0].handleInput(dt);

        field.renderGrid();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        field.renderCards(batch);
        hands[0].draw(batch);
        batch.end();

        stage.act(dt);
        stage.draw();

        //reset mouse click event
        mouseClicked = false;
	}

    //TODO: Come back to resizing
//    @Override
//    public void resize(int width, int height) {
//        WINDOW_WIDTH = width;
//        WINDOW_HEIGHT = height;
//        camera.setToOrtho(false, width, height);
//    }

    @Override
	public void dispose() {
		batch.dispose();
        stage.dispose();
        skin.dispose();
	}

    public static boolean clicked() {
        return mouseClicked;
    }

    @Override
    public boolean keyDown(int keycode) {
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
        if (button == Input.Buttons.LEFT) {
            mouseDown.set(screenX, screenY);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector2 current = new Vector2(screenX, screenY);
        if (button == Input.Buttons.LEFT && current.dst(mouseDown) <= Utils.sx(5)) {
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

    public static void info(String message) {
        Gdx.app.log("YGO", message);
    }

    public static void debug(String message) {
        Gdx.app.debug("YGO", message);
    }

    public static void showCardMenu(Card card) {
        currentlySelectedCard = card;
        isCardMenuShowing = true;
        switch (card.cardType) {
            case MONSTER:
                monsterTable.setVisible(true);
                monsterTable.setPosition(Utils.getMousePos().x, Utils.getMousePos().y);
                break;
        }
    }

    public static void hideCardMenus() {
        monsterTable.setVisible(false);
        isCardMenuShowing = false;
    }

    public static void performNormalSummon() {
        performSummon(SummonType.NORMAL_SUMMON, CardPlayMode.FACE_UP | CardPlayMode.ATTACK_MODE);
    }

    public static void performEffectActivation() {
        info("performEffectActivation not implemented");
    }

    public static void performSet() {
        performSummon(SummonType.SET, CardPlayMode.FACE_DOWN | CardPlayMode.DEFENSE_MODE);
    }

    private static void performSummon(SummonType summonType, int cardPlayMode) {
        Hand hand = hands[turnPlayer.index];
        if (currentlySelectedCard.location == Location.HAND) {
            hand.removeCard(currentlySelectedCard);
        }
        field.placeCardOnField(currentlySelectedCard, ZoneType.MONSTER, turnPlayer, cardPlayMode);
    }
}
