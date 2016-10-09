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
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.ygo.game.Utils;
import com.ygo.game.YGO;
import com.ygo.game.YGOServer;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

                final ReentrantLock lock = new ReentrantLock();
                final Condition cond = lock.newCondition();
                final Condition networkingDone = lock.newCondition();
                final Server server = createServer(lock, cond);
                final Client client = createClient(lock, cond);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        lock.lock();
                        networkingDone.awaitUninterruptibly();
                        lock.unlock();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                StateManager.pushState(new PlayState(server, client));
                            }
                        });
                    }
                }).start();

                Utils.sleep(10);
                lock.lock();
                networkingDone.signal();
                lock.unlock();

//                Timer.schedule(new Timer.Task() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 2);
            }
        });

        Tex

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

    private Server createServer(final Lock lock, final Condition cond) {
        final Server server = new Server();
        server.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.sleep(10);
                    lock.lock();
                    server.bind(27000);
                    cond.signal();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    lock.unlock();
                }
            }
        }).start();

        lock.lock();
        cond.awaitUninterruptibly();
        lock.unlock();
        return server;
    }

    private Client createClient(final Lock lock, final Condition cond) {
        final Client client = new Client();
        client.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.sleep(10);
                    lock.lock();
                    client.connect(3000, "localhost", YGOServer.PORT);
                    cond.signal();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    lock.unlock();
                }
            }
        }).start();

        lock.lock();
        cond.awaitUninterruptibly();
        lock.unlock();
        return client;
    }
}
