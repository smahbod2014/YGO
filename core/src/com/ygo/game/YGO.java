package com.ygo.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.ygo.game.GameStates.MenuState;
import com.ygo.game.GameStates.StateManager;

public class YGO extends ApplicationAdapter {

    public static int WINDOW_WIDTH = 960;
    public static int WINDOW_HEIGHT = WINDOW_WIDTH * 9 / 16;
    public static final int GAME_WIDTH = 1280;
    public static final int GAME_HEIGHT = 720;
    public static BitmapFont cardStatsFont;
    public static Sprite targetingCursor;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/calibri.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 18;
//        params.genMipMaps = true;
        params.magFilter = Texture.TextureFilter.Linear;
        params.minFilter = Texture.TextureFilter.Linear;
//        params.borderWidth = 1.25f;
        cardStatsFont = generator.generateFont(params);
//        cardStatsFont.getData().setScale(1.0f);
        generator.dispose();

        Card.FACE_DOWN_CARD_TEXTURE = new TextureRegion(new Texture("cards/cover.jpg"));

        TargetingCursor.cursor = new Texture("targeting_cursor.png");
        Explosion.spritesheet = new Texture("explosion_spritesheet.png");
        AttackSwordVisual.sword = new Texture("attack_sword.png");
        Cannonball.cannonball = new Texture("attack_sword.png");

        StateManager.pushState(new MenuState());
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float dt = Gdx.graphics.getDeltaTime();
        StateManager.update(dt);
        StateManager.render();
    }

    @Override
    public void resize(int width, int height) {
        StateManager.resize(width, height);
    }

    @Override
    public void dispose() {
        StateManager.dispose();
        cardStatsFont.dispose();
    }

    public static void info(String message) {
        Gdx.app.log("YGO", message);
    }

    public static void debug(String message) {
        Gdx.app.debug("YGO", message);
    }
}
