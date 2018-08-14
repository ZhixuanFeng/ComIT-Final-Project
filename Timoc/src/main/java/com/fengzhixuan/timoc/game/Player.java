package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.websocket.message.game.GameCardPileMessage;
import com.fengzhixuan.timoc.websocket.message.game.GamePlayerMessage;
import com.fengzhixuan.timoc.websocket.message.game.MessageType;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.*;

public class Player
{
    // value passed from GameController to Game to this, used for sending messages to players
    private SimpMessageSendingOperations messagingTemplate;

    // stores existing players (users who have entered a room or a game)
    // mapped by username
    private static Map<String, Player> players = new HashMap<>();
    // store starter cards separately in static to save memory
    // GameController initializes this at application launch
    private static Card[] starters;

    private String name;
    private String code;  // current room/game code
    private Map<Integer, Card> deck;  // information of player's deck, key is card indecks, value is the card
    private List<Integer> drawPile;  // indecks of the cards in the draw pile
    private List<Integer> handPile;  // indecks of the cards in the hand pile
    private List<Integer> discardPile;  // indecks of the cards in the discard pile
    private int hp;  // current hp of the player, hp = 0 means dead
    private int maxHp;  // maximum hp
    private int mana;  // current mana of the player
    private int maxMana;  // maximum mana
    private int hate;  // current hate of the player, enemies only target player with the highest hate
    private int drawNum;  // how many cards the player draws at the start of a round

    // statistic attributes
    private int damageDealt;  // records the total damage the player has dealt to enemies
    private int damageBlocked;  // records the total damage the player has blocked
    private int damageTaken;  // records the total damage the player has taken
    private int damageHealed;  // records the total damage the player has healed themselves and teammates
    private int manaRestored;  // records the total mana the player has restored to the player and teammates

    public Player(String name, String code, Map<Integer, Card> deck)
    {
        this.name = name;
        this.code = code;
        this.deck = deck;

        players.put(name, this);
    }

    // triggered when game starter, initialize attributes
    public void onGameStart(SimpMessageSendingOperations messagingTemplate)
    {
        this.messagingTemplate = messagingTemplate;

        drawPile = new ArrayList<>();
        handPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        for (int i = 0; i < 52; i++) drawPile.add(i);  // generate numbers which represent cards in a deck
        Collections.shuffle(drawPile);  // shuffle

        hp = maxHp = 100;
        mana = maxMana = 100;
        hate = 0;
        drawNum = 5;
        damageDealt = damageBlocked = damageHealed = manaRestored = 0;
    }

    // triggered in new round phase, discard remaining cards in hand, draw new cards
    public void onRoundStart()
    {
        // discard
        discardPile.addAll(handPile);
        handPile.clear();

        // draw
        drawCards(drawNum);

        messagingTemplate.convertAndSendToUser(name, "/topic/game/" + code, new GameCardPileMessage(getHandIndeckses()));
    }

    public void onTurnStart()
    {
        messagingTemplate.convertAndSend("/topic/game" + code, new GamePlayerMessage(MessageType.PlayerStartsTurn, name));
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
    public Card[] getHand()
    {
        Card[] hand = new Card[handPile.size()];
        for (int i = 0; i < hand.length; i++)
        {
            hand[i] = getCardByIndecks(handPile.get(i));
        }
        return hand;
    }

    // returns indeckses of the cards in hand pile
    public int[] getHandIndeckses()
    {
        int[] hand = new int[handPile.size()];
        for (int i = 0; i < hand.length; i++)
        {
            hand[i] = handPile.get(i);
        }
        return hand;
    }

    // get a card from the player's deck
    private Card getCardByIndecks(int indecks)
    {
        // if player has a non-starter card with indecks in the deck
        if (deck.containsKey(indecks))
        {
            return deck.get(indecks);
        }
        // if not, then player is using starter card for this indecks
        else
        {
            return starters[indecks];
        }
    }

    public static Player findPlayerByName(String name)
    {
        return players.containsKey(name) ? players.get(name) : null;
    }

    public static void removePlayer(String name)
    {
        if (players.containsKey(name)) players.remove(name);
    }

    public String getName()
    {
        return name;
    }

    public String getCode()
    {
        return code;
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

    public int getDrawNum()
    {
        return drawNum;
    }

    public void setDrawNum(int drawNum)
    {
        this.drawNum = drawNum;
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

    public int getDamageTaken()
    {
        return damageTaken;
    }

    public void setDamageTaken(int damageTaken)
    {
        this.damageTaken = damageTaken;
    }

    public int getDamageHealed()
    {
        return damageHealed;
    }

    public void setDamageHealed(int damageHealed)
    {
        this.damageHealed = damageHealed;
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
