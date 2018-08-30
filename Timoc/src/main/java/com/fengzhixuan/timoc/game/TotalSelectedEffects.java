package com.fengzhixuan.timoc.game;

public class TotalSelectedEffects
{
    Card[] selectedCards;

    private int attack = 0;
    private int block = 0;
    private int heal = 0;
    private int mana = 0;
    private int aoe = 0;
    private int draw = 0;
    private int revive = 0;
    private int taunt = 0;

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
        }
    }

    public boolean doNeedToSelectTarget()
    {
        return attack > 0 || heal > 0 || mana > 0 || revive > 0;
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
}
