package com.ygo.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ygo.game.GameStates.MenuState;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Messages.PhaseChangeMessage;
import com.ygo.game.Messages.SpellTrapSetMessage;
import com.ygo.game.Messages.SummonMessage;

public class ServerListener extends Listener {

    public PlayState playState;
    public Server server;

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
    public void received(Connection connection, Object m) {
        if (m instanceof SummonMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof SpellTrapSetMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof PhaseChangeMessage) {
            server.sendToAllTCP(m);
        }
    }
}
