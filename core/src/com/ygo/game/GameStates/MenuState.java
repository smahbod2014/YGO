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
import com.ygo.game.ServerListener;
import com.ygo.game.Utils;
import com.ygo.game.YGO;
import com.ygo.game.YGOServer;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.ygo.game.YGO.debug;
import static com.ygo.game.YGO.info;

/**
 * Created by semahbod on 10/8/16.
 */
public class MenuState extends GameState {
    public static final Object lock = new Object();
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

        TextButton hostLocal = new TextButton("Host Local", skin);
        hostLocal.getLabel().setFontScale(1.2f);
        hostLocal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Dialog dialog = new Dialog("Creating game", skin);
                Table dialogTable = new Table();
                dialogTable.pad(30f, 50f, 30f, 50f);
                dialogTable.add(new Label("Waiting for another player to join...", skin));
                dialog.add(dialogTable);
                dialog.show(stage);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ServerListener listener = new ServerListener();
                        final Server server = createServer(listener);
                        listener.server = server;
                        synchronized (lock) {
                            try {
                                lock.wait();
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        final Client client = createClient();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                dialog.hide();
                                StateManager.pushState(new PlayState(server, listener, client));
                            }
                        });
                    }
                }).start();


//                Timer.schedule(new Timer.Task() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 2);
            }
        });

        TextButton join = new TextButton("Join Local", skin);
        join.getLabel().setFontScale(1.2f);
        join.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Dialog dialog = new Dialog("Joining game", skin);
                Table dialogTable = new Table();
                dialogTable.pad(30f, 50f, 30f, 50f);
                dialogTable.add(new Label("Please wait...", skin));
                dialog.add(dialogTable);
                dialog.show(stage);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Client client = createClient();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                dialog.hide();
                                if (client == null) {
                                    final Dialog error = new Dialog("Couldn't connect", skin);
                                    TextButton ok = new TextButton("OK", skin);
                                    ok.getLabel().setFontScale(1.2f);
                                    ok.setWidth(100);
                                    ok.setHeight(30);
//                                    errorTable.add(ok).align(Align.bottomRight);
                                    error.text("No active server found");
                                    error.button(ok);
//                                    error.add(errorTable);
                                    error.show(stage);
                                }
                                else {
                                    StateManager.pushState(new PlayState(client));
                                }
                            }
                        });
                    }
                }).start();
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
        table.add(hostLocal).width(200).height(50).padBottom(5f).row();
        table.add(join).width(200).height(50).padBottom(5f).row();
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

    private Server createServer(ServerListener listener) {
        final Server server = new Server();
        server.addListener(listener);
        server.start();
        try {
            server.bind(27000);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Utils.sleep(10);
//                    lock.lock();
//                    Utils.sleep(3000);
//                    server.bind(27000);
//                    cond.signal();
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    lock.unlock();
//                }
//            }
//        }).start();
//
//        lock.lock();
//        cond.awaitUninterruptibly();
//        lock.unlock();
        return server;
    }

    private Client createClient() {
        final Client client = new Client();
        client.start();
        try {
            client.connect(3000, "localhost", YGOServer.PORT);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Utils.sleep(10);
//                    lock.lock();
//                    client.connect(3000, "localhost", YGOServer.PORT);
//                    cond.signal();
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    lock.unlock();
//                }
//            }
//        }).start();
//
//        lock.lock();
//        cond.awaitUninterruptibly();
//        lock.unlock();
        return client;
    }
}
