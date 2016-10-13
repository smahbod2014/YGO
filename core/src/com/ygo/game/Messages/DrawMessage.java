package com.ygo.game.Messages;

import com.ygo.game.Types.PlayerType;

/**
 * Message specifying that <code>player</code> drew a card
 */
public class DrawMessage {
    public String player;

    public DrawMessage() {

    }

    public DrawMessage(PlayerType who) {
        player = who.name();
    }
}
