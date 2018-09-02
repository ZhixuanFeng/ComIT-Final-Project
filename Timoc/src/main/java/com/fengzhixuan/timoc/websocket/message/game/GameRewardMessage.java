package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Card;

public class GameRewardMessage extends GameMessage
{
    private Card[] cards;
    private int gold;

    public GameRewardMessage(Card[] cards, int gold)
    {
        super(MessageType.GameEndReward);
        this.cards = cards;
        this.gold = gold;
    }

    public Card[] getCards()
    {
        return cards;
    }

    public int getGold()
    {
        return gold;
    }
}
