package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;

public class GameEnemyCardMessage extends GameEnemyMessage
{
    protected Card[] cards;

    public GameEnemyCardMessage(int type, int id, Card[] cards)
    {
        super(type, id);
        this.cards = cards;
    }

    public Card[] getCards()
    {
        return cards;
    }
}
