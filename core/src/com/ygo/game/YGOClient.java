package com.ygo.game;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by semahbod on 10/8/16.
 */
public class YGOClient extends Listener {

    Client client;

    public YGOClient(final Lock lock, final Condition cond) {
        client = new Client();

    }

    public void terminate() {
        client.stop();
    }

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
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
