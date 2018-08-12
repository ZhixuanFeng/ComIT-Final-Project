package com.fengzhixuan.timoc.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.websocket.message.game.GameCardPileMessage;
import com.fengzhixuan.timoc.websocket.message.game.GameEnemyCardMessage;
import com.fengzhixuan.timoc.websocket.message.game.MessageType;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Enemy
{
    // value passed from GameController to Game to this, used for sending messages to players
    private SimpMessageSendingOperations messagingTemplate;

    private String code;
    private String name;
    private int id;

    private Card[] deck;
    private List<Integer> drawPile;  // indecks of the cards in the draw pile
    private List<Integer> handPile;  // indecks of the cards in the hand pile
    private List<Integer> discardPile;  // indecks of the cards in the discard pile

    private int hp;
    private int maxHp;

    private int drawNum;  // how many cards the enemy draws at the start of a round

    public Enemy(String code, String name, int id, SimpMessageSendingOperations messagingTemplate)
    {
        this.messagingTemplate = messagingTemplate;

        deck = Player.getStarters();
        drawPile = new ArrayList<>();
        handPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        for (int i = 0; i < deck.length; i++) drawPile.add(i);  // generate numbers which represent cards in a deck
        Collections.shuffle(drawPile);  // shuffle

        this.code = code;
        this.name = name;
        this.id = id;
        maxHp = 30;
        hp = maxHp;

        drawNum = 1;
    }

    public void onRoundStart()
    {
        // discard
        discardPile.addAll(handPile);
        handPile.clear();

        // draw
        drawCards(drawNum);

        messagingTemplate.convertAndSend("/topic/game/" + code, new GameEnemyCardMessage(MessageType.EnemyDrawCard, this, getHand()));
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
}
