package com.ygo.game.Messages;

import com.ygo.game.Types.Player;

public class NextPlayersTurnMessage {
    public String player;

    public NextPlayersTurnMessage(Player player) {
        this.player = player.name();
    }

    public NextPlayersTurnMessage() {

    }
}
