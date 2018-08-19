package com.fengzhixuan.timoc.websocket.message.game;

// used when player draws cards during their turn, not at round start
public class GamePlayerDrawCardMessage extends GameMessage
{
    int[] cards;

    public GamePlayerDrawCardMessage(int[] cards)
    {
        super(MessageType.PlayerDrawCard);
        this.cards = cards;
    }

    public int[] getCards()
    {
        return cards;
    }
}
