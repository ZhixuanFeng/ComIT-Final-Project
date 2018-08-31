package com.fengzhixuan.timoc.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.game.enums.PlayerClass;
import com.fengzhixuan.timoc.game.enums.RoundPhase;
import com.fengzhixuan.timoc.websocket.message.game.*;

import java.util.*;

public class Player
{
    // stores existing players (users who have entered a room or a game)
    // mapped by username
    private static Map<String, Player> players = new HashMap<>();
    // store starter cards separately in static to save memory
    // GameController initializes this at application launch
    private static Card[] starters;

    private Game game;
    private String name;
    private int id = -1;  // player id in this game
    private String code;  // current room/game code
    private PlayerClass playerClass;  // class of the player
    private Map<Integer, Card> deck;  // information of player's deck, key is card indecks, value is the card
    private List<Integer> drawPile;  // indecks of the cards in the draw pile
    private List<Integer> handPile;  // indecks of the cards in the hand pile
    private List<Integer> discardPile;  // indecks of the cards in the discard pile
    private int hp;  // current hp of the player, hp = 0 means dead
    private int maxHp;  // maximum hp
    private int mana;  // current mana of the player
    private int maxMana;  // maximum mana
    private int hate;  // current hate of the player, enemies only target player with the highest hate
    private int block;  // how much block the player currently has
    private int drawNum;  // how many cards the player draws at the start of a round
    private int replaceAllowance;  // how many more cards can be replaced in this turn
    private boolean isDown;  // is player dead?

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
    public void onGameStart(Game game)
    {
        this.game = game;
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

        // reset block and replace
        replaceAllowance = 2;
        block = 0;
    }

    public void onTurnStart()
    {
        // draw
        drawCards(drawNum);
        sortHand();

        game.addDisplayMessage(new GamePlayerMessage(MessageType.PlayerStartsTurn, id));
        game.addDisplayMessage(new GameCardsMessage(MessageType.Hand, getHandIndeckses()));
        game.addDisplayMessage(new DisplayStateMessage(game.getDisplayStates()));
    }

    // draw certain number of cards, meaning move cards from draw pile to hand pile, if draw pile is empty, shuffle discard pile to draw pile
    // return the array of cards drawn
    public int[] drawCards(int count)
    {
        // hand can not exceed five cards
        if (handPile.size() + count > 5)
        {
            count = 5 - handPile.size();
        }

        int[] cardsDrawn = new int[count];
        for (int i = 0; i < count; i++)
        {
            if (drawPile.size() == 0)
            {
                shuffleDiscardPileIntoDrawPile();
            }
            int indecks = drawPile.remove(drawPile.size()-1);
            handPile.add(indecks);
            cardsDrawn[i] = indecks;
        }

        // update front end
        if (game.getPhase() == RoundPhase.PlayerTurn)  // don't send when player is starting their turn and drawing initial 5 cards
            game.addDisplayMessage(new GameCardsMessage(MessageType.PlayerDrawCard, cardsDrawn));
        return cardsDrawn;
    }

    private void sortHand()
    {
        // sort cards in hand
        if (handPile.size() > 1)
        {
            for (int i = 0; i < handPile.size(); i++)
            {
                for (int j = 1; j < handPile.size() - i; j++)
                {
                    int r1 = Card.indecksToRank(handPile.get(j-1));
                    int r2 = Card.indecksToRank(handPile.get(j));
                    if (r1 > r2 || r1 == r2 && Card.indecksToSuit(handPile.get(j-1)) > Card.indecksToSuit(handPile.get(j)))
                    {
                        int c = handPile.get(j-1);
                        handPile.set(j - 1, handPile.get(j));
                        handPile.set(j, c);
                    }
                }
            }
        }
    }

    // move a single card from hand pile to discard pile
    public void removeCard(int indecks)
    {
        int i = 0;
        while (handPile.get(i) != indecks && i < handPile.size()) i++;
        discardPile.add(handPile.remove(i));
        game.addDisplayMessage(new GameMessage(MessageType.RemoveUsedCard));
    }

    // move an array of cards from hand pile to discard pile
    public void removeCards(Card[] cards)
    {
        for (Card card : cards)
        {
            int i = 0;
            while (handPile.get(i) != card.getIndecks() && i < handPile.size()) i++;
            discardPile.add(handPile.remove(i));
        }
        game.addDisplayMessage(new GameMessage(MessageType.RemoveUsedCard));
    }

    // put discard pile into draw pile, then shuffle draw pile
    private void shuffleDiscardPileIntoDrawPile()
    {
        drawPile = discardPile;
        discardPile = new ArrayList<>();
        Collections.shuffle(drawPile);
    }

    // get cards in hand, sorted
    @JsonIgnore
    public Card[] getHand()
    {
        Card[] hand = new Card[handPile.size()];
        for (int i = 0; i < hand.length; i++)
        {
            hand[i] = getCardByIndecks(handPile.get(i));
        }
        return hand;
    }

    // returns only the indeckses of the cards in hand pile, sorted
    @JsonIgnore
    public int[] getHandIndeckses()
    {
        Card[] hand = getHand();
        int[] indecks = new int[hand.length];
        for (int i = 0; i < indecks.length; i++)
        {
            indecks[i] = hand[i].getIndecks();
        }
        return indecks;
    }

    // get a card from the player's deck
    public Card getCardByIndecks(int indecks)
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

    // update the amount of block the player currently has
    public void increaseBlock(int amount)
    {
        block += amount;
        if (block > 99) block = 99;

        if (amount > 0)
            game.addDisplayMessage(new GamePlayerIntMessage(MessageType.PlayerBlockChange, id, amount));
    }

    public void takeDamage(int amount)
    {
        int reducedBlock = 0;
        int reducedHp = 0;
        if (hp + block <= amount)
        {
            reducedBlock = block;
            reducedHp = hp;
            block = 0;
            hp = 0;
            isDown = true;
            recordDamageTaken(hp+block);
        }
        else
        {
            if (block > amount)
            {
                reducedBlock = amount;
                block -= amount;
            }
            else
            {
                reducedBlock = block;
                reducedHp = amount - block;
                hp -= reducedHp;
                block = 0;
            }
            recordDamageTaken(amount);
        }

        // update front end display
        if (reducedBlock > 0)
            game.addDisplayMessage(new GamePlayerIntMessage(MessageType.PlayerBlockChange, id, -reducedBlock));
        if (reducedHp > 0)
            game.addDisplayMessage(new GamePlayerIntMessage(MessageType.PlayerHpChange, id, -reducedHp));
    }

    // return actual amount healed
    public int heal(int amount)
    {
        if (isDown) return 0;

        int healedAmount = 0;
        if (hp + amount > maxHp)
        {
            healedAmount = maxHp - hp;
            hp = maxHp;
        }
        else
        {
            hp += amount;
            healedAmount = amount;
        }

        // update front end display
        if (healedAmount > 0)
            game.addDisplayMessage(new GamePlayerIntMessage(MessageType.PlayerHpChange, id, healedAmount));

        return healedAmount;
    }

    // return actual amount restored
    public int restoreMana(int amount)
    {
        if (isDown) return 0;

        int restoredAmount = 0;
        if (mana + amount > maxMana)
        {
            restoredAmount = maxMana - mana;
            mana = maxMana;
        }
        else
        {
            mana += amount;
            restoredAmount = amount;
        }

        // update front end display
        if (restoredAmount > 0)
            game.addDisplayMessage(new GamePlayerIntMessage(MessageType.PlayerManaChange, id, restoredAmount));

        return restoredAmount;
    }

    public void consumeMana(int amount)
    {
        // no need to check if mana is enough because it's checked as the command comes in
        mana -= amount;
        game.addDisplayMessage(new GamePlayerIntMessage(MessageType.PlayerManaChange, id, -amount));
    }

    public void increaseHate(int amount, String source)
    {
        switch (source)
        {
            case "taunt":
                hate += amount;
                break;
            case "attack":
                hate += amount;
                break;
            case "heal":
                hate += amount;
                break;
        }

        // update front end display
        if (amount > 0)
            game.addDisplayMessage(new GamePlayerIntMessage(MessageType.PlayerHateChange, id, amount));
    }

    // return actual amount healed on revive
    public int revive(int amount)
    {
        if (!isDown) return 0;

        isDown = false;
        return heal(amount);
    }

    public static Player findPlayerByName(String name)
    {
        return players.getOrDefault(name, null);
    }

    public static void removePlayer(String name)
    {
        players.remove(name);
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        if (this.id == -1) this.id = id;  // can be set only once
    }

    @JsonIgnore
    public String getCode()
    {
        return code;
    }

    public PlayerClass getPlayerClass()
    {
        return PlayerClass.Knight;
    }

    @JsonIgnore
    public Card[] getDeck()
    {
        return deck.values().toArray(new Card[0]);
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

    public int getBlock()
    {
        return block;
    }

    @JsonIgnore
    public int getDrawNum()
    {
        return drawNum;
    }

    public void setDrawNum(int drawNum)
    {
        this.drawNum = drawNum;
    }

    @JsonIgnore
    public int getReplaceAllowance()
    {
        return replaceAllowance;
    }

    public void setReplaceAllowance(int replaceAllowance)
    {
        this.replaceAllowance = replaceAllowance;
    }

    public boolean isDown()
    {
        return isDown;
    }

    public void setDown(boolean down)
    {
        isDown = down;
    }

    @JsonIgnore
    public int getDamageDealt()
    {
        return damageDealt;
    }

    public void recordDamageDealt(int damageDealt)
    {
        this.damageDealt += damageDealt;
    }

    @JsonIgnore
    public int getDamageBlocked()
    {
        return damageBlocked;
    }

    public void recordDamageBlocked(int damageBlocked)
    {
        this.damageBlocked += damageBlocked;
    }

    @JsonIgnore
    public int getDamageTaken()
    {
        return damageTaken;
    }

    public void recordDamageTaken(int damageTaken)
    {
        this.damageTaken += damageTaken;
    }

    @JsonIgnore
    public int getDamageHealed()
    {
        return damageHealed;
    }

    public void recordDamageHealed(int damageHealed)
    {
        this.damageHealed += damageHealed;
    }

    @JsonIgnore
    public int getManaRestored()
    {
        return manaRestored;
    }

    public void recordManaRestored(int manaRestored)
    {
        this.manaRestored += manaRestored;
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

    @JsonIgnore
    public List<Integer> getHandPile()
    {
        return handPile;
    }
}
