package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;
import com.fengzhixuan.timoc.game.Enemy;

public class GameEnemyCardMessage extends GameMessage
{
    protected int id;
    protected Card[] cards;

    public GameEnemyCardMessage(MessageType type, int id, Card[] cards)
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
