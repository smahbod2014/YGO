package com.ygo.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ygo.game.GameStates.MenuState;
import com.ygo.game.GameStates.StateManager;
import com.ygo.game.Tests.Tests;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.SummonType;
import com.ygo.game.Types.ZoneType;
import com.ygo.game.listeners.ActivateButtonListener;
import com.ygo.game.listeners.NormalSummonButtonListener;
import com.ygo.game.listeners.SetButtonListener;
import static  com.ygo.game.Types.CardType.*;

public class YGO extends ApplicationAdapter {

    public static int WINDOW_WIDTH = 960;
    public static int WINDOW_HEIGHT = WINDOW_WIDTH * 9 / 16;
    public static final int GAME_WIDTH = 1280;
    public static final int GAME_HEIGHT = 720;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        Card.FACE_DOWN_CARD_TEXTURE = new TextureRegion(new Texture("cards/cover.jpg"));

        CardManager.add("3573512", normalMonster(), 500, 1200, 3); //Swordsman of Landstar
        CardManager.add("7489323", effectMonster(), 300, 250, 1); //Milus Radiant
        CardManager.add("80770678", normalMonster(), 800, 2000, 4); //Spirit of the Harp
        CardManager.add("88819587", normalMonster(), 1200, 700, 3); //Baby Dragon
        CardManager.add("93013676", effectMonster(), 1550, 1400, 4); //Maha Vailo
        CardManager.add("yugi/4031928", normalSpell()); //Change of Heart
        CardManager.add("yugi/4206964", normalTrap()); //Trap Hole
        CardManager.add("yugi/6368038", normalMonster(), 2300, 2100, 7); //Gaia the fierce knight
        CardManager.add("yugi/12607053", normalTrap()); //Waboku
        CardManager.add("yugi/13039848", normalMonster(), 1300, 2000, 3); //Giant soldier of stone
        CardManager.add("yugi/13429800", normalMonster(), 1600, 800, 4); //Great white
        CardManager.add("yugi/13723605", normalMonster(), 1600, 1000, 4); //Man-eating tresure chest
        CardManager.add("yugi/13945283", effectMonster(), 1000, 1850, 4); //Wall of illusion
        CardManager.add("yugi/15025844", normalMonster(), 800, 2000, 4); //Mystical elf
        CardManager.add("yugi/16972957", normalMonster(), 1600, 1400, 5); //Doma the angel of silence
        CardManager.add("yugi/32452818", normalMonster(), 1200, 1500, 4); //Beaver warrior
        CardManager.add("yugi/32807846", normalSpell()); //Reinforcement of the army
        CardManager.add("yugi/36304921", normalMonster(), 1400, 1300, 4); //Witty phantom
        CardManager.add("yugi/37120512", equipSpell()); //Sword of dark destruction


        StateManager.pushState(new MenuState());

        if (CardManager.get("yugi/13945283").isMonster()) {
            debug("Correct");
        }
        else {
            debug("incorrect");
        }
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
    }

    public static void info(String message) {
        Gdx.app.log("YGO", message);
    }

    public static void debug(String message) {
        Gdx.app.debug("YGO", message);
    }
}
