package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;
import com.fengzhixuan.timoc.game.Enemy;

public class GameEnemyCardMessage extends GameEnemyMessage
{
    protected Card[] cards;

    public GameEnemyCardMessage(MessageType type, Enemy enemy, Card[] cards)
    {
        super(type, enemy);
        this.cards = cards;
    }

    public Card[] getCards()
    {
        return cards;
    }
}
