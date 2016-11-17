package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ygo.game.GameStates.MenuState;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Messages.AttackInitiationMessage;
import com.ygo.game.Messages.AttackMessage;
import com.ygo.game.Messages.DirectAttackInitiationMessage;
import com.ygo.game.Messages.DirectAttackMessage;
import com.ygo.game.Messages.DrawMessage;
import com.ygo.game.Messages.NextPlayersTurnMessage;
import com.ygo.game.Messages.PhaseChangeMessage;
import com.ygo.game.Messages.RetaliatoryDamageMessage;
import com.ygo.game.Messages.SpellTrapSetMessage;
import com.ygo.game.Messages.SummonMessage;
import com.ygo.game.Messages.TestMessage;
import com.ygo.game.Types.Phase;
import com.ygo.game.Types.PlayerType;

import static com.ygo.game.YGO.debug;

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
        if (m instanceof FrameworkMessage.KeepAlive)
            return;

        if (m instanceof SummonMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof SpellTrapSetMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof PhaseChangeMessage) {
            server.sendToAllTCP(m);
            PhaseChangeMessage p = (PhaseChangeMessage) m;
            if (Phase.valueOf(p.newPhase) == Phase.END_PHASE) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        final PlayerType nextPlayer = playState.turnPlayer.getOpponent();
                        DelayedEvents.schedule(new Runnable() {
                            @Override
                            public void run() {
                                server.sendToAllTCP(new NextPlayersTurnMessage(nextPlayer));
                                debug("Server: Sending NextPlayersTurnMessage. Now " + nextPlayer.toString() + "'s turn");
                            }
                        }, 1);

                        DelayedEvents.schedule(new Runnable() {
                            @Override
                            public void run() {
                                server.sendToAllTCP(new PhaseChangeMessage(Phase.DRAW_PHASE));

                            }
                        }, 2);
                    }
                });
            }
        }
        else if (m instanceof AttackMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof AttackInitiationMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof RetaliatoryDamageMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof DrawMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof DirectAttackInitiationMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof DirectAttackMessage) {
            server.sendToAllTCP(m);
        }
        else if (m instanceof TestMessage) {
            server.sendToTCP(connection.getID(), m);
        }
    }
}
