package com.ygo.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by semahbod on 10/8/16.
 */
public class YGOServer extends Listener {

    public static final int PORT = 27000;
    Server server;

    public YGOServer(final Lock lock, final Condition cond) {

    }

    public void terminate() {
        server.stop();
    }

    @Override
    public void connected(Connection connection) {
        System.out.println("Received a connection");
    }

    @Override
    public void disconnected(Connection connection) {
    }

    @Override
    public void received(Connection connection, Object object) {
    }
}
