package com.fengzhixuan.timoc.websocket.message.game;

public class GameCardsMessage extends GameMessage
{
    private int[] cards;

    public GameCardsMessage(int type, int[] cards)
    {
        super(type);
        this.cards = cards;
    }

    public int[] getCards()
    {
        return cards;
    }
}
