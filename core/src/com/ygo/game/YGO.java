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
import com.ygo.game.Types.PlayerType;

public class YGO extends ApplicationAdapter {

	public static int WINDOW_WIDTH = 300;
    public static int WINDOW_HEIGHT = 169;

    static OrthographicCamera camera;
	SpriteBatch batch;
    Field field;
    Hand p1Hand, p2Hand;

    Skin skin;
    Stage stage;
    Table table;
	
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
        table = new Table();
        TextButton b = new TextButton("Test", skin);
        b.setWidth(WINDOW_WIDTH * 0.1f);
        b.setHeight(WINDOW_HEIGHT * 0.05f);
        //scale the button text here
        stage.addActor(b);
        b.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                info("Button clicked");
            }
        });

        p1Hand = new Hand(0.75f);
        p1Hand.addCard(new Card("75646520"));
        p1Hand.addCard(new Card("75652080"));
        p1Hand.addCard(new Card("75673220"));
        p1Hand.addCard(new Card("75675029"));
        p1Hand.addCard(new Card("75732622"));

        Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float dt = Gdx.graphics.getDeltaTime();
        p1Hand.handleInput(dt);

        field.renderGrid();
        batch.begin();
        field.renderCards(batch);
        p1Hand.draw(batch, PlayerType.CURRENT_PLAYER);
        batch.end();

        stage.act(dt);
        stage.draw();

        if (Gdx.input.justTouched()) {
            Gdx.app.log("YGO", "Game touched");
        }
	}

    @Override
	public void dispose() {
		batch.dispose();
        stage.dispose();
        skin.dispose();
	}

    public static void info(String message) {
        Gdx.app.log("YGO", message);
    }
}
