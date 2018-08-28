package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;

public class GameDeckMessage extends GamePlayerMessage
{
    private Card[] cards;

    public GameDeckMessage(MessageType type, String name, Card[] cards)
    {
        super(type, name);
        this.cards = cards;
    }

    public Card[] getCards()
    {
        return cards;
    }
}
