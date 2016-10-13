package com.ygo.game.Messages;

import com.ygo.game.Types.PlayerType;

public class NextPlayersTurnMessage {
    public String player;

    public NextPlayersTurnMessage(PlayerType player) {
        this.player = player.name();
    }

    public NextPlayersTurnMessage() {

    }
}
