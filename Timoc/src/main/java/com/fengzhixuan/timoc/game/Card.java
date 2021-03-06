package com.fengzhixuan.timoc.game;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

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

    public static Card cardEntityToCard(com.fengzhixuan.timoc.data.entity.Card card)
    {
        return new Card(card.getIndecks(), card.getSuit(), card.getRank(), card.getAttack(), card.getBlock(), card.getHeal(), card.getMana(), card.getAoe(), card.getDraw(), card.getRevive(), card.getTaunt());
    }

    public static Map<Integer, Card> cardEntitiesToCards(Map<Integer, com.fengzhixuan.timoc.data.entity.Card> entities)
    {
        Map<Integer, Card> cards = new HashMap<>();
        entities.forEach((key, value) -> {
            cards.put(key, cardEntityToCard(value));
        });

        return cards;
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

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getAttack()
    {
        return attack;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getBlock()
    {
        return block;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getHeal()
    {
        return heal;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getMana()
    {
        return mana;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getAoe()
    {
        return aoe;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getDraw()
    {
        return draw;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getRevive()
    {
        return revive;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getTaunt()
    {
        return taunt;
    }

    public static int indecksToSuit(int indecks)
    {
        return indecks / 13;
    }

    public static int indecksToRank(int indecks)
    {
        return indecks - (indecks/13) * 13 + 1;
    }
}
