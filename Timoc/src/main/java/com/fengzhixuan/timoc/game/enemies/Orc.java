package com.fengzhixuan.timoc.game.enemies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.game.Card;
import com.fengzhixuan.timoc.game.Enemy;
import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Orc extends Enemy
{
    public Orc(Game game, int id, int position)
    {
        super(game, "orc", id, position);
    }

    @Override
    protected void initStats()
    {
        maxHp = 30;
        hp = maxHp;
        str = 0;
        drawNum = 1;

        gold = game.nextInt(5) + 7 + id;
    }

    // orc is a simple enemy, it's not necessary to save a deck in memory for it
    @Override
    protected void initDeck() { }

    @Override
    protected void initCardPiles()
    {
        drawPile = new ArrayList<>();
        handPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        for (int i = 0; i < 52; i++) drawPile.add(i);  // generate numbers which represent cards in a deck
        Collections.shuffle(drawPile);  // shuffle
    }

    @Override
    public void onTurnStart()
    {
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

            // all cards are attack cards, rank 1-4 have 2 attack, 5-9 have 4 attack, 10-13 have 6 attack
            hand[i] = new Card(indecks, suit, rank, ((rank/5)+1)*2, 0, 0, 0, 0, 0, 0, 0);
        }

        return hand;
    }
}
