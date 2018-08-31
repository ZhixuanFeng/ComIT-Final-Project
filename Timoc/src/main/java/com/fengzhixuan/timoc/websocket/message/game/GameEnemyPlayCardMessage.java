package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;

public class GameEnemyPlayCardMessage extends GameEnemyMessage
{
    private Card card;
    private String target;  // if it's player, target is player name, if enemy, target is enemy id

    public GameEnemyPlayCardMessage(int id, Card card, String target)
    {
        super(MessageType.EnemyPlaysCard, id);
        this.card = card;
        this.target = target;
    }

    public Card getCard()
    {
        return card;
    }

    public String getTarget()
    {
        return target;
    }
}
