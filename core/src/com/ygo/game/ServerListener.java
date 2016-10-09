package com.ygo.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ygo.game.GameStates.MenuState;
import com.ygo.game.GameStates.PlayState;

public class ServerListener extends Listener {

    public PlayState playState;

    @Override
    public void connected(Connection connection) {
        synchronized (MenuState.lock) {
            MenuState.lock.notify();
        }
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
    }

    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);
    }
}
