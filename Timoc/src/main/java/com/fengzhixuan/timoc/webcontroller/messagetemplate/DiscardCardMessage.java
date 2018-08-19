package com.fengzhixuan.timoc.webcontroller.messagetemplate;

import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;

import java.util.List;

public class DiscardCardMessage
{
    private int[] cards;

    public DiscardCardMessage()
    {
    }

    public DiscardCardMessage(int[] cards)
    {
        this.cards = cards;
    }

    public boolean isValid(Game game, Player player)
    {
        // check if card indecks are in range
        // TODO: in the future when it gets more complicated to validate this, move this to doesPlayerHaveCard(int[]) in Player.java
        for (int card : cards)
        {
            if (card > 51 || card < 0)
            {
                return false;
            }
        }

        // check if the player has cards with these indeckses in hand
        for (int card : cards)
        {
            List<Integer> hand = player.getHandPile();
            if (hand.indexOf(card) < 0)
            {
                return false;
            }
        }

        return true;
    }

    public int[] getCards()
    {
        return cards;
    }
}
