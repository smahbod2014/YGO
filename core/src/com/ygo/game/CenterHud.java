package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CenterHud {

    private enum State { SOLID, FADING, DONE }

    Label label;
    Skin skin;
    Stage stage;
    float elapsedTime;
    float solidTime, fadeTime;
    State state;

    public CenterHud(OrthographicCamera camera) {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas("ui/uiskin.atlas"));
        stage = new Stage(new StretchViewport(YGO.GAME_WIDTH, YGO.GAME_HEIGHT, camera));
        label = new Label("Placeholder", skin);
        label.setFontScale(3);
//        label.setAlignment(Align.center);
        label.setFillParent(true);
        label.setVisible(false);
        stage.addActor(label);
    }

    public void setPosition(float x, float y) {
        label.setPosition(x, y);
    }

    public void flash(String text, float solidDuration, float fadeDuration) {
        label.setVisible(true);
        label.setText(text);
        solidTime = solidDuration;
        fadeTime = fadeDuration;
        elapsedTime = 0;
        state = State.SOLID;
        label.setColor(1, 1, 1, 1);
    }

    public void update(float dt) {
        if (state == State.SOLID && elapsedTime < solidTime) {
            elapsedTime += dt;
            if (elapsedTime >= solidTime) {
                state = State.FADING;
                elapsedTime = 0;
            }
        }
        else if (state == State.FADING && elapsedTime < fadeTime) {
            float alpha = 1 - elapsedTime / fadeTime;
            label.setColor(1, 1, 1, alpha);
            elapsedTime += dt;
            if (elapsedTime >= fadeTime) {
                state = State.DONE;
                label.setVisible(false);
            }
        }
        stage.act(dt);
    }

    public void render() {
        stage.draw();
    }
}
