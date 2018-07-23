package com.fengzhixuan.timoc.service.game;

public class Card
{
    private int indecks;
    private int suit;
    private int rank;
    private int attack;
    private int block;
    private int heal;
    private int mana;
    private int aoe;
    private int draw;
    private int revive;
    private int taunt;

    public Card(int indecks, int suit, int rank, int attack, int block, int heal, int mana, int aoe, int draw, int revive, int taunt)
    {
        this.indecks = indecks;
        this.suit = suit;
        this.rank = rank;
        this.attack = attack;
        this.block = block;
        this.heal = heal;
        this.mana = mana;
        this.aoe = aoe;
        this.draw = draw;
        this.revive = revive;
        this.taunt = taunt;
    }

    public int getIndecks()
    {
        return indecks;
    }

    public int getSuit()
    {
        return suit;
    }

    public int getRank()
    {
        return rank;
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

    public int getAoe()
    {
        return aoe;
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
