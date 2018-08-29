package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;

public class GameDeckMessage extends GameMessage
{
    private int id;
    private Card[] cards;

    public GameDeckMessage(int type, int id, Card[] cards)
    {
        super(type);
        this.id = id;
        this.cards = cards;
    }

    public int getId()
    {
        return id;
    }

    public Card[] getCards()
    {
        return cards;
    }
}
