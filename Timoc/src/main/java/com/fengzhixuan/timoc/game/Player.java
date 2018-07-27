package com.fengzhixuan.timoc.game;

import java.util.Map;

public class Player
{
    private String name;

    private Map<Integer, Card> deck;

    private static Card[] starters; // store starter cards separately in static to save memory
    private static Map<Long, Player> players; // stores existing players (users who have entered a room or a game)

    private int hp;
    private int maxHp;
    private int mana;
    private int maxMana;
    private int hate;

    private int damageDealt;
    private int damageBlocked;
    private int damageHeal;
    private int manaRestored;

    public Player(long id, String name, Map<Integer, Card> deck)
    {
        this.name = name;
        this.deck = deck;

        hp = maxHp = 100;
        mana = maxMana = 100;
        hate = 0;
        damageDealt = damageBlocked = damageHeal = manaRestored = 0;

        players.put(id, this);
    }

    public String getName()
    {
        return name;
    }

    public Map<Integer, Card> getDeck()
    {
        return deck;
    }

    public int getHp()
    {
        return hp;
    }

    public void setHp(int hp)
    {
        this.hp = hp;
    }

    public int getMaxHp()
    {
        return maxHp;
    }

    public void setMaxHp(int maxHp)
    {
        this.maxHp = maxHp;
    }

    public int getMana()
    {
        return mana;
    }

    public void setMana(int mana)
    {
        this.mana = mana;
    }

    public int getMaxMana()
    {
        return maxMana;
    }

    public void setMaxMana(int maxMana)
    {
        this.maxMana = maxMana;
    }

    public int getHate()
    {
        return hate;
    }

    public void setHate(int hate)
    {
        this.hate = hate;
    }

    public int getDamageDealt()
    {
        return damageDealt;
    }

    public void setDamageDealt(int damageDealt)
    {
        this.damageDealt = damageDealt;
    }

    public int getDamageBlocked()
    {
        return damageBlocked;
    }

    public void setDamageBlocked(int damageBlocked)
    {
        this.damageBlocked = damageBlocked;
    }

    public int getDamageHeal()
    {
        return damageHeal;
    }

    public void setDamageHeal(int damageHeal)
    {
        this.damageHeal = damageHeal;
    }

    public int getManaRestored()
    {
        return manaRestored;
    }

    public void setManaRestored(int manaRestored)
    {
        this.manaRestored = manaRestored;
    }

    public static Card[] getStarters()
    {
        return starters;
    }

    public static void setStarters(Card[] starters)
    {
        if (Player.starters == null)
        {
            Player.starters = starters;
        }
    }
}
