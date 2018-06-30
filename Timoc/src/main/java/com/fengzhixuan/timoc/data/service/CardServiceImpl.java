package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.Player;
import com.fengzhixuan.timoc.data.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CardServiceImpl implements CardService
{
    @Autowired
    private CardRepository cardRepository;

    @Override
    public void saveCard(Card card)
    {
        cardRepository.save(card);
    }

    @Override
    public void deleteCard(Card card)
    {
        cardRepository.delete(card);
    }

    @Override
    public void setOwner(Card card, Player player)
    {
        card.setPlayer(player);
        cardRepository.save(card);
    }

    @Override
    public Card createCard(int meanQuality, int deviation)
    {
        Random random = new Random();
        int quality = Math.round((float)random.nextGaussian() * deviation + meanQuality); // normal distribution
        if (quality < 0) quality = 0;
        if (quality > 100) quality = 100;
        float qualityMultiplier = 0.75f + quality * 0.0125f;
        int suit = random.nextInt(4);
        int rank = random.nextInt(13) + 1;

        Card card = new Card();
        card.setQuality(quality);
        card.setSuit(suit);
        card.setRank(rank);

        /* create random card effects
         *
         * Main effects of suits:
         * Suit      | Primary               | Secondary
         * ----------------------------------------------
         * Spade     | block                 | taunt
         * Heart     | heal                  | mana, revive
         * Club      | attack, block         | draw
         * Diamond   | attack                | AOE
         *
         * Effect generation rules:
         * 40% chance of cards with primary effects only;
         * 40% chance of cards with mixed effects of primary and secondary effects
         * 20% chance of cards with mixed effects from all effects
        */

        int r = random.nextInt(100);
        float cardValue = rank * qualityMultiplier;
        if (r < 40)
        {
            switch (card.getSuitEnum())
            {
                case Spade:
                    card.setBlock(Math.round(cardValue));
                    break;
                case Heart:
                    card.setHeal(Math.round(cardValue));
                    break;
                case Club:
                    card.setAttack(Math.round(cardValue / 2));
                    card.setBlock(Math.round(cardValue / 2));
                    break;
                case Diamond:
                    card.setAttack(Math.round(cardValue));
                    break;
                default:
                    return null;
            }
        }
        else if (r < 80)
        {
            switch (card.getSuitEnum())
            {
                case Spade:
                    // taunt amount = (0-100%) of card value
                    int taunt = random.nextInt(Math.round(cardValue)) + 1;
                    card.setTaunt(taunt);
                    card.setBlock(Math.round(cardValue) - taunt);
                    break;
                case Heart:
                    // 20% revive + heal; 80% heal + mana
                    card.setHeal(Math.round(cardValue));
                    int r2 = random.nextInt(100);
                    if (r2 < 20)
                    {
                        int revive = random.nextInt(Math.round(cardValue)) + 1;
                        card.setRevive(revive * 2);
                        card.setHeal(Math.round(cardValue) - revive);
                    }
                    else
                    {
                        int mana = random.nextInt(Math.round(cardValue)) + 1;
                        card.setMana(mana);
                        card.setHeal(Math.round(cardValue) - mana);
                    }
                    break;
                case Club:
                    // 40% draw 1 + attack + block; 60% attack + block
                    if (random.nextInt(100) < 40)
                    {
                        //int draw = (int)(random.nextInt(Math.round(cardValue)) / 20f) + 1;
                        int draw = 1;
                        card.setDraw(draw);
                        int remainingValue = Math.round(cardValue) - 10;
                        if (remainingValue > 0)
                        {
                            int attack = random.nextInt(remainingValue) + 1;
                            card.setAttack(attack);
                            card.setBlock(remainingValue - attack);
                        }
                    }
                    else
                    {
                        int attack = random.nextInt(Math.round(cardValue)) + 1;
                        card.setAttack(attack);
                        card.setBlock(Math.round(cardValue) - attack);
                    }
                    break;
                case Diamond:
                    // 75% with aoe effect, 25% without
                    if (random.nextInt(100) < 75)
                    {
                        int aoe = 1;
                        card.setAoe(aoe);
                        int remainingValue = Math.round(cardValue);
                        if (remainingValue > 0)
                        {
                            int attack = random.nextInt(remainingValue);
                            card.setAttack(attack);
                        }
                    }
                    else
                    {
                        card.setAttack(Math.round(cardValue));
                    }
                    break;
                default:
                    return null;
            }
        }
        else
        {
            int remainingValue = Math.round(cardValue);
            int effectCount = 0;
            while (remainingValue > 0 && effectCount < 4)
            {
                int r2 = random.nextInt(11);
                int value = random.nextInt(remainingValue) + 1;
                switch (r2)
                {
                    case 0: // attack
                        if (card.getHeal() == 0 || card.getAoe() > 0)
                        {
                            if (card.getAttack() == 0) effectCount++;
                            card.setAttack(card.getAttack() + value);
                            remainingValue -= value;
                        }
                        break;
                    case 1: // block
                        if (card.getBlock() == 0) effectCount++;
                        card.setBlock(card.getBlock() + value);
                        remainingValue -= value;
                        break;
                    case 2: // heal
                        if (card.getAttack() == 0 || card.getAoe() > 0)
                        {
                            if (card.getHeal() == 0) effectCount++;
                            card.setHeal(card.getHeal() + value);
                            remainingValue -= value;
                        }
                        break;
                    case 3: // taunt
                        if (card.getTaunt() == 0) effectCount++;
                        card.setTaunt(card.getTaunt() + value);
                        remainingValue -= value;
                        break;
                    case 4: // mana
                        if (card.getMana() == 0) effectCount++;
                        card.setMana(card.getMana() + value);
                        remainingValue -= value;
                        break;
                    case 5: // draw
                    case 6: // draw
                        if (card.getDraw() == 0)
                        {
                            effectCount++;
                            card.setDraw(1);
                            remainingValue -= 10;
                        }
                        break;
                    case 7: // aoe
                    case 8: // aoe
                        if (card.getAoe() == 0)
                        {
                            effectCount++;
                            card.setAoe(1);
                        }
                        break;
                    case 9: // revive
                    case 10: // revive
                        if (card.getAttack() == 0 || card.getAoe() > 0)
                        {
                            if (card.getRevive() == 0) effectCount++;
                            card.setRevive(card.getRevive() + value * 2);
                            remainingValue -= value;
                        }
                        break;
                    default:
                        return null;
                }
            }
        }
        return card;
    }
}
