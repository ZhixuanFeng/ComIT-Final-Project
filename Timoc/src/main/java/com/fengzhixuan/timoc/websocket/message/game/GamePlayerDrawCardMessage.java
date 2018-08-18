package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;

// used when player draws cards during their turn, not at round start
public class GamePlayerDrawCardMessage extends GameMessage
{
    Card[] cards;

    public GamePlayerDrawCardMessage(Card[] cards)
    {
        super(MessageType.PlayerDrawCard);
        this.cards = cards;
    }

    public Card[] getCards()
    {
        return cards;
    }
}
