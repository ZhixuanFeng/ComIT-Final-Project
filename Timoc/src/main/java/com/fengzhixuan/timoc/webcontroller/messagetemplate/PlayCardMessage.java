package com.fengzhixuan.timoc.webcontroller.messagetemplate;

import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.game.enums.TargetingMode;

import java.util.List;

public class PlayCardMessage
{
    private int[] cards;
    private int mode;
    private String target;

    public PlayCardMessage()
    {
    }

    public PlayCardMessage(int[] cards, int mode)
    {
        this.cards = cards;
        this.mode = mode;
    }

    public PlayCardMessage(int[] cards, int mode, String target)
    {
        this.cards = cards;
        this.mode = mode;
        this.target = target;
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

        // check if the player has enough mana to play these cards
        int totalManaCost = 0;
        for (int indecks : cards)
        {
            totalManaCost += indecks - ((int)(indecks / 13)) * 13 + 1;
        }
        if (totalManaCost > player.getMana())
        {
            return false;
        }

        // check if mode is in range
        if (mode < 0 || mode > 4)
        {
            return false;
        }

        // check if player with name stored in target can be found
        if (mode == TargetingMode.player.ordinal() && !game.isPlayerInThisGame(target))
        {
            return false;
        }
        // check if enemy with id stored in target can be found
        else if (mode == TargetingMode.enemy.ordinal() && !game.isEnemyTargetable(Integer.parseInt(target)))
        {
            return false;
        }

        return true;
    }

    public int[] getCards()
    {
        return cards;
    }

    public int getMode()
    {
        return mode;
    }

    public String getTarget()
    {
        return target;
    }
}
