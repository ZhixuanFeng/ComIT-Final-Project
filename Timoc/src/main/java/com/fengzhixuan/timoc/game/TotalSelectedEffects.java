package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.game.enums.TargetingMode;

public class TotalSelectedEffects
{
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

    public TotalSelectedEffects(Card[] selectedCards)
    {
        this.selectedCards = selectedCards;
        for (Card card : selectedCards)
        {
            attack += card.getAttack();
            block += card.getBlock();
            heal += card.getHeal();
            mana += card.getMana();
            aoe += card.getAoe();
            draw += card.getDraw();
            revive += card.getRevive();
            taunt += card.getTaunt();

            manaCost += card.getRank();
        }

        if (aoe > 0)
        {
            // effect is halved due to aoe deduction
            attack = Math.round((float) attack / 2);
            heal = Math.round((float) heal / 2);
            mana = Math.round((float) mana / 2);
            revive = Math.round((float) revive / 2);
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
}
