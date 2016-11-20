package com.ygo.game.Messages;

import com.ygo.game.Types.PlayerType;

import java.util.UUID;

public class CardActivationMessage {
    public String activator;
    public UUID cardId;

    public CardActivationMessage(PlayerType activator, UUID cardId) {
        this.activator = activator.name();
        this.cardId = cardId;
    }

    public CardActivationMessage() {}
}
