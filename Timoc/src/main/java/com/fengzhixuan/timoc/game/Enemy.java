package com.fengzhixuan.timoc.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Enemy
{
    // value passed from GameController to Game to this, used for sending messages to players
    protected SimpMessageSendingOperations messagingTemplate;

    protected Game game;
    protected String code;
    protected String name;
    protected int id;

    protected static Card[] deck;
    protected List<Integer> drawPile;  // indecks of the cards in the draw pile
    protected List<Integer> handPile;  // indecks of the cards in the hand pile
    protected List<Integer> discardPile;  // indecks of the cards in the discard pile

    protected int hp;
    protected int maxHp;
    protected int str;

    protected int drawNum;  // how many cards the enemy draws at the start of a round
    protected boolean dead;

    public Enemy(Game game, String code, String name, int id, SimpMessageSendingOperations messagingTemplate)
    {
        this.messagingTemplate = messagingTemplate;

        this.game = game;
        this.code = code;
        this.name = name;
        this.id = id;

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
        deck = Player.getStarters();
    }

    protected void initCardPiles()
    {
        drawPile = new ArrayList<>();
        handPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        for (int i = 0; i < deck.length; i++) drawPile.add(i);  // generate numbers which represent cards in a deck
        Collections.shuffle(drawPile);  // shuffle
    }

    public void onRoundStart()
    {
        // discard
        discardPile.addAll(handPile);
        handPile.clear();

        // draw
        drawCards(drawNum);

        messagingTemplate.convertAndSend("/topic/game/" + code, new GameEnemyCardMessage(MessageType.EnemyDrawCard, id, getHand()));
    }

    public List<GameMessage> onTurnStart()
    {
        if (dead) return null;

        List<GameMessage> messages = new ArrayList<>();
        Player mostHatePlayer = game.findPlayerWithMostHate();
        for (Card card : getHand())
        {
            if (card.getAttack() > 0)
            {
                messages.add(new GameEnemyPlayCardMessage(id, card, mostHatePlayer.getName()));
                if (card.getAoe() > 0)
                {
                    for (Map.Entry<String, Player> playerEntry : game.getPlayers().entrySet())
                    {
                        playerEntry.getValue().takeDamage(card.getAttack());
                    }
                    messages.add(new GameIntMessage(MessageType.AllPlayerTakeDamage, card.getAttack()));
                }
                else
                {
                    mostHatePlayer.takeDamage(card.getAttack());
                    messages.add(new GameIntMessage(MessageType.PlayerTakesDamage, card.getAttack()));
                }
            }
            else if (card.getHeal() > 0)
            {
                messages.add(new GameEnemyPlayCardMessage(id, card, "" + id));
                if (card.getAoe() > 0)
                {
                    for (Map.Entry<Integer, Enemy> enemyEntry : game.getEnemies().entrySet())
                    {
                        enemyEntry.getValue().heal(card.getHeal());
                    }
                    messages.add(new GameIntMessage(MessageType.AllEnemyHeal, card.getHeal()));
                }
                else
                {
                    heal(card.getHeal());
                    messages.add(new GameIntMessage(MessageType.Enemyheal, card.getHeal()));
                }
            }
        }
        return messages;
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
    private void shuffleDiscardPileIntoDrawPile()
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
        if (hp < amount)
        {
            damageTaken = hp;
            hp = 0;
            dead = true;
        }
        else
        {
            damageTaken = amount;
            hp -= amount;
        }
        return damageTaken;
    }

    public void heal(int amount)
    {
        if (dead) return;

        if (hp + amount > maxHp)
        {
            hp = maxHp;
        }
        else
        {
            hp += amount;
        }
    }


    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
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
