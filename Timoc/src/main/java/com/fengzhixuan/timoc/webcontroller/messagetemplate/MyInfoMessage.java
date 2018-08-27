package com.fengzhixuan.timoc.webcontroller.messagetemplate;

// message template for the case when player asking for info of themselves
public class MyInfoMessage
{
    private long id;
    private String name;
    private int level;
    private int gold;
    private int maxCard;
    private int cardsOwned;

    public MyInfoMessage(long id, String name, int level, int gold, int maxCard, int cardsOwned)
    {
        this.id = id;
        this.name = name;
        this.level = level;
        this.gold = gold;
        this.maxCard = maxCard;
        this.cardsOwned = cardsOwned;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getLevel()
    {
        return level;
    }

    public int getGold()
    {
        return gold;
    }

    public int getMaxCard()
    {
        return maxCard;
    }

    public int getCardsOwned()
    {
        return cardsOwned;
    }
}
