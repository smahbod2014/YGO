package com.ygo.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.ygo.game.Messages.DrawMessage;
import com.ygo.game.Messages.GameInitializationMessage;
import com.ygo.game.Messages.NextPlayersTurnMessage;
import com.ygo.game.Messages.PhaseChangeMessage;
import com.ygo.game.Messages.SpellTrapSetMessage;
import com.ygo.game.Messages.SummonMessage;

public class ClientListener extends Listener {

    public PlayState playState;

    public ClientListener(PlayState playState) {
        this.playState = playState;
    }

    @Override
    public void received(Connection connection, final Object m) {
        if (m instanceof FrameworkMessage.Ping)
            return;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (m instanceof GameInitializationMessage) {
                    playState.handleGameInitializationMessage((GameInitializationMessage) m);
                }
                else if (m instanceof DrawMessage) {
                    playState.handleDrawMessage((DrawMessage) m);
                }
                else if (m instanceof SummonMessage) {
                    playState.handleSummonMessage((SummonMessage) m);
                }
                else if (m instanceof SpellTrapSetMessage) {
                    playState.handleSpellTrapSetMessage((SpellTrapSetMessage) m);
                }
                else if (m instanceof PhaseChangeMessage) {
                    playState.handlePhaseChangeMessage((PhaseChangeMessage) m);
                }
                else if (m instanceof NextPlayersTurnMessage) {
                    playState.handleNextPlayersTurnMessage((NextPlayersTurnMessage) m);
                }
            }
        });
    }
}
