package com.fengzhixuan.timoc.game.enemies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.game.Card;
import com.fengzhixuan.timoc.game.Enemy;
import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.websocket.message.game.GameEnemyCardMessage;
import com.fengzhixuan.timoc.websocket.message.game.MessageType;

public class TimocOdium extends Enemy
{
    private int bonusDrawNum = -1;

    public TimocOdium(Game game, int id, int position)
    {
        super(game, "timocodium", id, position);
    }

    @Override
    protected void initStats()
    {
        maxHp = 270 + 20 * game.getPlayers().size();
        hp = maxHp;
        str = 0;
        drawNum = 2;

        gold = game.nextInt(5) + 50 + id;
    }

    @Override
    public void onRoundStart()
    {
        // discard
        discardPile.addAll(handPile);
        handPile.clear();

        // draw more and more cards
        // pattern: 2, 3, 4, 5,  3, 4, 5,  3, 4, 5,  3, 4, 5...
        bonusDrawNum++;
        if (drawNum + bonusDrawNum > 5)
        {
            bonusDrawNum = 1;
            str++;
        }

        // draw
        drawCards(drawNum + bonusDrawNum);

        game.addDisplayMessage(new GameEnemyCardMessage(MessageType.EnemyDrawsCard, id, getHand()));
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

            // heart cards are aoe
            int aoe = 0;
            if (suit == 2) aoe = 1;
            hand[i] = new Card(indecks, suit, rank, 7, 0, 0, 0, aoe, 0, 0, 0);
        }

        return hand;
    }

    @Override
    public int takeDamage(int amount, Player source)
    {
        int damageTaken = super.takeDamage(amount, source);
        source.takeDamage(drawNum);
        return damageTaken;
    }

    @Override
    protected void die()
    {
        super.die();
        game.finishPlayerTurn();
        game.victory();
    }
}
