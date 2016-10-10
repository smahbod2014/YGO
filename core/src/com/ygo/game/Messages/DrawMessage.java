package com.ygo.game.Messages;

/**
 * Message specifying that <code>player</code> drew a card
 */
public class DrawMessage {
    public int player;

    public DrawMessage() {

    }

    public DrawMessage(int who) {
        player = who;
    }
}
