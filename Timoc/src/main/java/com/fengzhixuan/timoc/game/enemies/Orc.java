package com.fengzhixuan.timoc.game.enemies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.game.Card;
import com.fengzhixuan.timoc.game.Enemy;
import com.fengzhixuan.timoc.game.Game;

public class Orc extends Enemy
{
    public Orc(Game game, int id, int position)
    {
        super(game, "orc", id, position);
    }

    @Override
    protected void initStats()
    {
//        maxHp = 40 + 10 * game.getPlayers().size();
        maxHp = 1;
        hp = maxHp;
        str = 0;
        drawNum = 1;

        gold = game.nextInt(5) + 7 + id;
    }

    @Override
    public void onTurnStart()
    {
        str++;
        super.onTurnStart();
    }

    // get cards in hand
    @JsonIgnore
    public Card[] getHand()
    {
        Card[] hand = new Card[handPile.size()];
        for (int i = 0; i < hand.length; i++)
        {
            int indecks = handPile.get(i);
            int suit = Card.indecksToSuit(indecks);
            int rank = Card.indecksToRank(indecks);

            // all cards are attack cards, rank 1-4 have 5 attack, 5-9 have 6 attack, 10-13 have 7 attack
            hand[i] = new Card(indecks, suit, rank, ((rank/5)+1)+4, 0, 0, 0, 0, 0, 0, 0);
        }

        return hand;
    }
}
