package com.fengzhixuan.timoc.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.websocket.message.game.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Enemy
{

    protected Game game;
    protected String name;
    protected int id;
    protected int position;

    protected static Card[] deck;
    protected List<Integer> drawPile;  // indecks of the cards in the draw pile
    protected List<Integer> handPile;  // indecks of the cards in the hand pile
    protected List<Integer> discardPile;  // indecks of the cards in the discard pile

    protected int hp;
    protected int maxHp;
    protected int str;

    protected int drawNum;  // how many cards the enemy draws at the start of a round
    protected boolean dead;

    protected int gold;  // how much gold reward for killing this enemy

    public Enemy(Game game, String name, int id, int position)
    {
        this.game = game;
        this.name = name;
        this.id = id;
        this.position = position;

        initStats();
        initDeck();
        initCardPiles();
    }

    protected void initStats()
    {
        maxHp = 30;
        hp = maxHp;
        str = 0;
        drawNum = 1;
    }

    protected void initDeck()
    {

    }

    protected void initCardPiles()
    {
        drawPile = new ArrayList<>();
        handPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        for (int i = 0; i < 52; i++) drawPile.add(i);  // generate numbers which represent cards in a deck
        Collections.shuffle(drawPile);  // shuffle
    }

    public void onRoundStart()
    {
        // discard
        discardPile.addAll(handPile);
        handPile.clear();

        // draw
        drawCards(drawNum);

        game.addDisplayMessage(new GameEnemyCardMessage(MessageType.EnemyDrawsCard, id, getHand()));
    }

    public void onTurnStart()
    {
        if (dead || game.isGameOver()) return;

        game.addDisplayMessage(new GameEnemyMessage(MessageType.EnemyStartsTurn, id));

        Player mostHatePlayer = game.findPlayerWithMostHate();
        if (mostHatePlayer == null) return;
        for (Card card : getHand())
        {
            if (card.getAttack() > 0)
            {
                game.addDisplayMessage(new GameEnemyPlayCardMessage(MessageType.EnemyPlaysCard_Player, id, card, mostHatePlayer.getId()));
                int attack = card.getAttack();
                if (card.getAoe() > 0)
                {
                    attack = Math.round((float) attack / 2 + str);
                    for (Map.Entry<String, Player> playerEntry : game.getPlayers().entrySet())
                    {
                        playerEntry.getValue().takeDamage(card.getAttack()/2);
                    }
                }
                else
                {
                    mostHatePlayer.takeDamage(attack + str);
                }
            }
            else if (card.getHeal() > 0)
            {
                game.addDisplayMessage(new GameEnemyPlayCardMessage(MessageType.EnemyPlaysCard_Enemy, id, card, id));
                if (card.getAoe() > 0)
                {
                    for (Map.Entry<Integer, Enemy> enemyEntry : game.getEnemies().entrySet())
                    {
                        enemyEntry.getValue().heal(card.getHeal()/2);
                    }
                }
                else
                {
                    heal(card.getHeal());
                }
            }
            if (game.isGameOver()) return;
        }

        game.addDisplayMessage(new GameEnemyMessage(MessageType.EnemyEndsTurn, id));

        return;
    }

    // draw certain number of cards, meaning move cards from draw pile to hand pile, if draw pile is empty, shuffle discard pile to draw pile
    public void drawCards(int count)
    {
        for (int i = 0; i < count; i++)
        {
            if (drawPile.size() == 0)
            {
                shuffleDiscardPileIntoDrawPile();
            }
            handPile.add(drawPile.remove(drawPile.size()-1));
        }
    }

    // put discard pile into draw pile, then shuffle draw pile
    protected void shuffleDiscardPileIntoDrawPile()
    {
        drawPile = discardPile;
        discardPile = new ArrayList<>();
        Collections.shuffle(drawPile);
    }

    // get cards in hand
    @JsonIgnore
    public Card[] getHand()
    {
        Card[] hand = new Card[handPile.size()];
        for (int i = 0; i < hand.length; i++)
        {
            hand[i] = deck[handPile.get(i)];
        }
        return hand;
    }

    // return actual damage taken
    public int takeDamage(int amount, Player source)
    {
        int damageTaken;
        if (hp <= amount)
        {
            damageTaken = hp;
            hp = 0;
            die();
        }
        else
        {
            damageTaken = amount;
            hp -= amount;
        }

        // update front end display
        if (damageTaken > 0)
            game.addDisplayMessage(new GameEnemyIntMessage(MessageType.EnemyHpChange, id, -damageTaken));
        return damageTaken;
    }

    public void heal(int amount)
    {
        if (dead) return;

        int amountHealed = 0;
        if (hp + amount > maxHp)
        {
            amountHealed = maxHp - hp;
            hp = maxHp;
        }
        else
        {
            amountHealed = amount;
            hp += amount;
        }

        // update front end display
        if (amountHealed > 0)
            game.addDisplayMessage(new GameEnemyIntMessage(MessageType.EnemyHpChange, id, amountHealed));
    }

    protected void die()
    {
        dead = true;
        game.addDisplayMessage(new GameEnemyMessage(MessageType.EnemyDies, id));
        game.addGoldRewardAllPlayers(gold);
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public int getPosition()
    {
        return position;
    }

    public int getHp()
    {
        return hp;
    }

    public int getMaxHp()
    {
        return maxHp;
    }

    public int getStr()
    {
        return str;
    }

    public boolean isDead()
    {
        return dead;
    }
}
