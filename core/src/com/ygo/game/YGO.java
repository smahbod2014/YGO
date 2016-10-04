package com.ygo.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.PlayerType;

public class YGO extends ApplicationAdapter {

	public static int WINDOW_WIDTH = 1280;
    public static int WINDOW_HEIGHT = 720;

    static boolean isCardMenuShowing;
    static OrthographicCamera camera;
	SpriteBatch batch;
    Field field;
    Hand p1Hand, p2Hand;

    Skin skin;
    Stage stage;
    static Table monsterTable;
	
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

        p1Hand = new Hand(0.75f, PlayerType.CURRENT_PLAYER);
        p1Hand.addCard(new Card("75646520", CardType.TRAP));
        p1Hand.addCard(new Card("75652080", CardType.SPELL));
        p1Hand.addCard(new Card("75673220", CardType.MONSTER));
        p1Hand.addCard(new Card("75675029", CardType.MONSTER));
        p1Hand.addCard(new Card("75732622", CardType.MONSTER));

        Gdx.input.setInputProcessor(stage);
	}

    private void initCardMenus() {
        monsterTable = new Table();
        TextButton activate = new TextButton("Active", skin);
        activate.setWidth(Utils.sx(100));
        activate.setHeight(Utils.sy(30));
        activate.getLabel().setFontScale(Utils.getCurrentWindowScaleX(), Utils.getCurrentWindowScaleY());
        activate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                info("Active clicked");
            }
        });

        TextButton ns = new TextButton("Normal Summon", skin);
        ns.setWidth(Utils.sx(100));
        ns.setHeight(Utils.sy(30));
        ns.getLabel().setFontScale(Utils.getCurrentWindowScaleX(), Utils.getCurrentWindowScaleY());
        ns.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                info("Normal Summon clicked");
            }
        });

        TextButton set = new TextButton("Set", skin);
        set.setWidth(Utils.sx(100));
        set.setHeight(Utils.sy(30));
        set.getLabel().setFontScale(Utils.getCurrentWindowScaleX(), Utils.getCurrentWindowScaleY());
        set.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                info("Set clicked");
            }
        });

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
        p1Hand.handleInput(dt);

        field.renderGrid();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        field.renderCards(batch);
        p1Hand.draw(batch);
        batch.end();

        stage.act(dt);
        stage.draw();

        if (Gdx.input.justTouched()) {
            Gdx.app.log("YGO", "Game touched");
        }
	}

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

    public static void info(String message) {
        Gdx.app.log("YGO", message);
    }

    public static void showCardMenu(Card card) {
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
}
