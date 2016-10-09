package com.ygo.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ygo.game.YGO;

import static com.ygo.game.YGO.info;

/**
 * Created by semahbod on 10/8/16.
 */
public class MenuState extends GameState {

    OrthographicCamera camera;
    Stage stage;
    Skin skin;
    Table table;

    public MenuState() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, YGO.GAME_WIDTH, YGO.GAME_HEIGHT);
        stage = new Stage(new StretchViewport(YGO.GAME_WIDTH, YGO.GAME_HEIGHT, camera));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas("ui/uiskin.atlas"));
        table = new Table();

        TextButton playLocal = new TextButton("Host", skin);
        playLocal.getLabel().setFontScale(1.2f);
        playLocal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Dialog dialog = new Dialog("Connecting", skin);
                Table dialogTable = new Table();
                dialogTable.pad(30f, 50f, 30f, 50f);
                dialogTable.add(new Label("Please wait...", skin));
                dialog.add(dialogTable);
                dialog.show(stage);

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        dialog.cancel();
                        StateManager.pushState(new PlayState());
                    }
                }, 2);
            }
        });

        TextButton quit = new TextButton("Quit", skin);
        quit.getLabel().setFontScale(1.2f);
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.setFillParent(true);
        table.center();
        table.add(playLocal).width(200).height(50).padBottom(5f).row();
        table.add(quit).width(200).height(50).row();
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render() {
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}