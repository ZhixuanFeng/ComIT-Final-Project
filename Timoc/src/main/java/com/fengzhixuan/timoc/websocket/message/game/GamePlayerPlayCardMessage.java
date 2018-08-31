package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;

public class GamePlayerPlayCardMessage extends GamePlayerMessage
{
    private int targetId;
    private int[] cards;

    public GamePlayerPlayCardMessage(int type, int id, int targetId, Card[] cards)
    {
        super(type, id);
        this.targetId = targetId;
        this.cards = new int[cards.length];
        for (int i = 0; i < cards.length; i++)
        {
            this.cards[i] = cards[i].getIndecks();
        }
    }

    public int getTargetId()
    {
        return targetId;
    }

    public int[] getCards()
    {
        return cards;
    }
}
