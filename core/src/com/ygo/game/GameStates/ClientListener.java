package com.ygo.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.ygo.game.Messages.GameInitializationMessage;

public class ClientListener extends Listener {

    public PlayState playState;

    public ClientListener(PlayState playState) {
        this.playState = playState;
    }

    @Override
    public void received(Connection connection, final Object object) {
        if (object instanceof FrameworkMessage.Ping)
            return;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (object instanceof GameInitializationMessage) {
                    GameInitializationMessage m = (GameInitializationMessage) object;
                    playState.handleGameInitializationMessage(m);
                }
            }
        });
    }
}
