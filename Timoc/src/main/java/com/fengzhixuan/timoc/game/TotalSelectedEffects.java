package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.game.enums.PlayerClass;
import com.fengzhixuan.timoc.game.enums.PokerHand;
import com.fengzhixuan.timoc.game.enums.TargetingMode;

public class TotalSelectedEffects
{
    private Player player;
    private Card[] selectedCards;
    private TargetingMode targetingMode;
    private int TargetPosition;

    private int attack = 0;
    private int block = 0;
    private int heal = 0;
    private int mana = 0;
    private int aoe = 0;
    private int draw = 0;
    private int revive = 0;
    private int taunt = 0;

    private int manaCost = 0;
    private PokerHand pokerHand;

    public TotalSelectedEffects(Player player, Card[] selectedCards)
    {
        this.player = player;
        this.selectedCards = selectedCards;
        pokerHand = Hand.identifyHand(selectedCards);
        for (Card card : selectedCards)
        {
            attack += card.getAttack() + player.getSTR();
            block += card.getBlock() + player.getVIT();
            heal += card.getHeal() + player.getINT();
            mana += card.getMana() + player.getINT();
            aoe += card.getAoe();
            draw += card.getDraw();
            revive += card.getRevive();
            taunt += card.getTaunt();

            manaCost += card.getRank();
        }

        // apply class bonus
        if (player.getPlayerClass() == PlayerClass.Knight)
        {
            block = Math.round(block * 1.5f);
        }
        else if (player.getPlayerClass() == PlayerClass.Wizard)
        {
            // draw a card for each pair played
            if (pokerHand == PokerHand.OnePair) draw++;
            else if (pokerHand == PokerHand.TwoPair ||
                    pokerHand == PokerHand.FullHouse ||
                    pokerHand == PokerHand.FourOfAKind) draw+=2;
        }
        else if (player.getPlayerClass() == PlayerClass.Berserker)
        {
            attack = Math.round(attack * 1.5f);
        }
        else if (player.getPlayerClass() == PlayerClass.Priest)
        {
            heal = Math.round(heal * 1.5f);
        }

        float effectMultiplier = 1f;
        switch (pokerHand)
        {
            case HighCard:
                break;
            case OnePair:
                effectMultiplier = 1.5f;
                break;
            case TwoPair:
                effectMultiplier = 2f;
                break;
            case ThreeOfAKind:
                revive += 10;
                effectMultiplier = 2f;
                break;
            case Straight:
                manaCost = 0;
                break;
            case Flush:
                break;
            case FullHouse:
                revive += 10;
                aoe = 1;
                effectMultiplier = 3f;
                break;
            case FourOfAKind:
                effectMultiplier = 3f;
                break;
            case StraightFlush:
                manaCost = 0;
                effectMultiplier = 3f;
                break;
            case RoyalFlush:
                manaCost = 0;
                effectMultiplier = 5f;
                break;
        }

        if (aoe > 0)
        {
            // effect is halved due to aoe deduction
            attack = Math.round((float) attack / 2);
            heal = Math.round((float) heal / 2);
            mana = Math.round((float) mana / 2);
            revive = Math.round((float) revive / 2);
        }

        if (effectMultiplier != 1)
        {
            attack = Math.round(attack * effectMultiplier);
            block = Math.round(block * effectMultiplier);
            heal = Math.round(heal * effectMultiplier);
            mana = Math.round(mana * effectMultiplier);
//            draw = Math.round(draw * effectMultiplier);
            revive = Math.round(revive * effectMultiplier);
            taunt = Math.round(taunt * effectMultiplier);
        }
    }

    public boolean doNeedToSelectTarget()
    {
        return attack > 0 || heal > 0 || mana > 0 || revive > 0;
    }

    public Card[] getSelectedCards()
    {
        return selectedCards;
    }

    public TargetingMode getTargetingMode()
    {
        return targetingMode;
    }

    public void setTargetingMode(TargetingMode targetingMode)
    {
        this.targetingMode = targetingMode;
    }

    public int getTargetPosition()
    {
        return TargetPosition;
    }

    public void setTargetPosition(int targetPosition)
    {
        TargetPosition = targetPosition;
    }

    public boolean isAoe()
    {
        return aoe > 0;
    }

    public int getAttack()
    {
        return attack;
    }

    public int getBlock()
    {
        return block;
    }

    public int getHeal()
    {
        return heal;
    }

    public int getMana()
    {
        return mana;
    }

    public int getDraw()
    {
        return draw;
    }

    public int getRevive()
    {
        return revive;
    }

    public int getTaunt()
    {
        return taunt;
    }

    public int getManaCost()
    {
        return manaCost;
    }

    public PokerHand getPokerHand()
    {
        return pokerHand;
    }
}
