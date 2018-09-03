package com.fengzhixuan.timoc.game.enemies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.game.Card;
import com.fengzhixuan.timoc.game.Enemy;
import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;

public class HighOrc extends Enemy
{
    public HighOrc(Game game, int id, int position)
    {
        super(game, "highorc", id, position);
    }

    @Override
    protected void initStats()
    {
        maxHp = 150 + 10 * game.getPlayers().size();
        hp = maxHp;
        str = 0;
        drawNum = 2;

        gold = game.nextInt(5) + 15 + id;
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

            // all cards are attack cards, if it's a K, it's aoe
            int aoe = 0;
            if (rank == 13) aoe = 1;
            hand[i] = new Card(indecks, suit, rank, 12, 0, 0, 0, aoe, 0, 0, 0);
        }

        return hand;
    }

    @Override
    public int takeDamage(int amount, Player source)
    {
        // negate damages smaller than 10
        if (amount < 10) amount = 0;
        if (amount != 0) str += 2;

        return super.takeDamage(amount, source);
    }
}
