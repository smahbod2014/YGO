package com.ygo.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.ygo.game.Messages.AttackInitiationMessage;
import com.ygo.game.Messages.AttackMessage;
import com.ygo.game.Messages.CardActivationMessage;
import com.ygo.game.Messages.DirectAttackInitiationMessage;
import com.ygo.game.Messages.DirectAttackMessage;
import com.ygo.game.Messages.DrawMessage;
import com.ygo.game.Messages.GameInitializationMessage;
import com.ygo.game.Messages.NextPlayersTurnMessage;
import com.ygo.game.Messages.PhaseChangeMessage;
import com.ygo.game.Messages.RetaliatoryDamageMessage;
import com.ygo.game.Messages.SpellTrapSetMessage;
import com.ygo.game.Messages.SummonMessage;
import com.ygo.game.Messages.TestMessage;
import com.ygo.game.Types.PlayerType;

public class ClientListener extends Listener {

    public PlayState playState;

    public ClientListener(PlayState playState) {
        this.playState = playState;
    }

    @Override
    public void received(Connection connection, final Object m) {
        if (m instanceof FrameworkMessage.KeepAlive)
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
                else if (m instanceof AttackMessage) {
                    playState.handleAttackMessage((AttackMessage) m);
                }
                else if (m instanceof AttackInitiationMessage) {
                    playState.handleAttackInitiationMessage((AttackInitiationMessage) m);
                }
                else if (m instanceof DirectAttackMessage) {
                    playState.handleDirectAttackMessage((DirectAttackMessage) m);
                }
                else if (m instanceof DirectAttackInitiationMessage) {
                    playState.handleDirectAttackInitiationMessage((DirectAttackInitiationMessage) m);
                }
                else if (m instanceof RetaliatoryDamageMessage) {
                    playState.handleRetaliatoryDamageMessage((RetaliatoryDamageMessage) m);
                }
                else if (m instanceof CardActivationMessage) {
                    playState.handleCardActivationMessage((CardActivationMessage) m);
                }
            }
        });
    }
}
