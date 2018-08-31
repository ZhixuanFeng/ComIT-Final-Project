package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;

public class GameEnemyPlayCardMessage extends GameEnemyMessage
{
    private Card card;
    private int targetId;

    public GameEnemyPlayCardMessage(int type, int id, Card card, int targetId)
    {
        super(type, id);
        this.card = card;
        this.targetId = targetId;
    }

    public Card getCard()
    {
        return card;
    }

    public int getTargetId()
    {
        return targetId;
    }
}
