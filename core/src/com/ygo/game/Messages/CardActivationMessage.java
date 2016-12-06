package com.ygo.game.Messages;

import com.ygo.game.Card;
import com.ygo.game.Types.Player;

import java.util.UUID;

public class CardActivationMessage {
    /** Who activated this card? */
    public String activator;
    /** If this card was activated in response to something, what was the offending card? */
    public UUID offendingCard;
    /** What card was activated? */
    public UUID cardId;

    public CardActivationMessage(Player activator, Card card) {
        this.activator = activator.name();
        this.cardId = card.getId();
    }

    public CardActivationMessage(Player activator, Card offendingCard, Card card) {
        this(activator, card);
        this.offendingCard = offendingCard.getId();
    }

    public CardActivationMessage() {}
}
