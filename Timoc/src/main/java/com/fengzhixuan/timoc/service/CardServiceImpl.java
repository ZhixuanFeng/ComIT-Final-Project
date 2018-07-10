package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.CardDeck;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
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
    public Card getCardById(long id)
    {
        return cardRepository.getOne(id);
    }

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
    public void setCollection(Card card, CardCollection collection)
    {
        if (collection != null)
        {
            card.setOwnerType(CardOwnerType.Player_Not_In_Deck);
        }
        else
        {
            card.setOwnerType(CardOwnerType.NoOwner);
        }
        card.setCardCollection(collection);
        cardRepository.save(card);
    }

    @Override
    public void setDeck(Card card, CardDeck deck)
    {
        card.setCardDeck(deck);
        card.setOwnerType(deck == null ? CardOwnerType.Player_Not_In_Deck : CardOwnerType.Player_In_Deck);
        cardRepository.save(card);
    }

    @Override
    public Card createCard(int meanQuality, int deviation)
    {
        Random random = new Random();
        int quality = Math.round((float)random.nextGaussian() * deviation + meanQuality); // normal distribution
        if (quality < 0) quality = 0;
        if (quality > 100) quality = 100;
        //float qualityMultiplier = 0.75f + quality * 0.0125f;
        float qualityMultiplier = 1 + quality / 100f;
        int suit = random.nextInt(4);
        int rank = random.nextInt(13) + 1;

        Card card = new Card();
        card.setQuality(quality);
        card.setIndex(suit, rank);

        /* create random card effects
         *
         * Main effects of suits:
         * Suit      | Primary               | Secondary
         * ----------------------------------------------
         * Spade     | block                 | taunt
         * Heart     | heal                  | mana, revive
         * Club      | attack, block         | random
         * Diamond   | attack                | AOE
         *
         * Effect generation rules:
         * 40% chance of allCards with primary effects only;
         * 40% chance of allCards with mixed effects of primary and secondary effects
         * 20% chance of allCards with mixed effects from all effects
        */

        int r = random.nextInt(100);
        int cardValue = Math.round(rank * qualityMultiplier);
        if (r < 40)
        {
            switch (card.getSuitEnum())
            {
                case Spade:
                    card.setBlock(cardValue);
                    break;
                case Heart:
                    card.setHeal(cardValue);
                    break;
                case Club:
                    card.setAttack(Math.round(cardValue / 2));
                    card.setBlock(cardValue - card.getAttack());
                    break;
                case Diamond:
                    card.setAttack(cardValue);
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
                    /*// taunt amount + block amount = card value
                    int block = random.nextInt(cardValue);
                    card.setBlock(block);
                    card.setTaunt(cardValue - block);*/
                    card.setBlock(cardValue);
                    card.setTaunt(random.nextInt(cardValue));
                    break;
                case Heart:
                    // 20% revive + heal; 80% heal + mana
                    if (random.nextInt(100) < 20)
                    {
                        int revive = random.nextInt(cardValue) + 1;
                        card.setRevive(revive * 2);
                        card.setHeal(cardValue - revive);
                    }
                    else
                    {
                        int mana = random.nextInt(cardValue) + 1;
                        card.setMana(mana);
                        card.setHeal(cardValue - card.getMana());
                    }
                    break;
                case Club:
                    // 40% random & attack & block; 60% attack & block
                    if (random.nextInt(100) < 40)
                    {
                        card.setRandom(random.nextInt(cardValue) + 1);
                        int remainingValue = cardValue - card.getRandom();
                        if (remainingValue > 0)
                        {
                            int attack = random.nextInt(remainingValue) + 1;
                            card.setAttack(attack);
                            card.setBlock(remainingValue - attack);
                        }
                    }
                    else
                    {
                        int attack = random.nextInt(cardValue) + 1;
                        card.setAttack(attack);
                        card.setBlock(cardValue - attack);
                    }
                    break;
                case Diamond:
                    // 75% with aoe effect, 25% without
                    if (random.nextInt(100) < 75)
                    {
                        card.setAoe(1);
                    }
                    card.setAttack(cardValue);
                    break;
                default:
                    return null;
            }
        }
        else
        {
            int remainingValue = cardValue;
            int effectCount = 0;
            while (remainingValue > 0 && effectCount < 4)
            {
                int r2 = random.nextInt(8);
                int value = random.nextInt(remainingValue) + 1;
                switch (r2)
                {
                    case 0: // attack
                        if (card.getAttack() == 0) effectCount++;
                        card.setAttack(card.getAttack() + value);
                        remainingValue -= value;
                        break;
                    case 1: // block
                        if (card.getBlock() == 0) effectCount++;
                        card.setBlock(card.getBlock() + value);
                        remainingValue -= value;
                        break;
                    case 2: // heal
                        if (card.getHeal() == 0) effectCount++;
                        card.setHeal(card.getHeal() + value);
                        remainingValue -= value;
                        break;
                    case 3: // taunt
                        if (card.getTaunt() == 0)
                        {
                            effectCount++;
                            remainingValue += value;
                        }
                        card.setTaunt(card.getTaunt() + value);
                        //remainingValue -= value;
                        break;
                    case 4: // mana
                        if (card.getMana() == 0) effectCount++;
                        card.setMana(card.getMana() + value);
                        remainingValue -= value;
                        break;
                    case 5: // random
                        if (card.getRandom() == 0) effectCount++;
                        card.setRandom(card.getRandom() + value);
                        remainingValue -= value;
                        break;
                    case 6: // aoe
                        if (card.getAoe() == 0)
                        {
                            effectCount++;
                            card.setAoe(1);
                        }
                        break;
                    case 7: // revive
                        if (card.getRevive() == 0) effectCount++;
                        card.setRevive(card.getRevive() + value * 2);
                        remainingValue -= value;
                        break;
                    default:
                        return null;
                }
            }
        }
        System.out.println(r + " Created card: " + card.toString());
        return card;
    }

    /*
     * return a card object containing starter card information
     * use this method to get information instead of querying the database to improve performance
     * ** if the information changes here, remember to change data.sql and starterCards.js as well **
     */
    @Override
    public Card getStarterCard(int indecks)
    {
        switch (indecks)
        {
            case 0:
                return new Card(indecks, 1, 0, 0, 0, 0, 0, 0, 0);
            case 1:
                return new Card(indecks, 2, 0, 0, 0, 0, 0, 0, 0);
            case 2:
                return new Card(indecks, 3, 0, 0, 0, 0, 0, 0, 0);
            case 3:
                return new Card(indecks, 4, 0, 0, 0, 0, 0, 0, 0);
            case 4:
                return new Card(indecks, 5, 0, 0, 0, 0, 0, 0, 0);
            case 5:
                return new Card(indecks, 6, 0, 0, 0, 0, 0, 0, 0);
            case 6:
                return new Card(indecks, 7, 0, 0, 0, 0, 0, 0, 0);
            case 7:
                return new Card(indecks, 8, 0, 0, 0, 0, 0, 0, 0);
            case 8:
                return new Card(indecks, 9, 0, 0, 0, 0, 0, 0, 0);
            case 9:
                return new Card(indecks, 10, 0, 0, 0, 0, 0, 0, 0);
            case 10:
                return new Card(indecks, 11, 0, 0, 0, 0, 0, 0, 0);
            case 11:
                return new Card(indecks, 12, 0, 0, 0, 0, 0, 0, 0);
            case 12:
                return new Card(indecks, 13, 0, 0, 0, 0, 0, 0, 1);
            case 13:
                return new Card(indecks, 0, 1, 0, 0, 0, 0, 0, 0);
            case 14:
                return new Card(indecks, 1, 1, 0, 0, 0, 0, 0, 0);
            case 15:
                return new Card(indecks, 2, 1, 0, 0, 0, 0, 0, 0);
            case 16:
                return new Card(indecks, 2, 2, 0, 0, 0, 0, 0, 0);
            case 17:
                return new Card(indecks, 2, 3, 0, 0, 0, 0, 0, 0);
            case 18:
                return new Card(indecks, 3, 3, 0, 0, 0, 0, 0, 0);
            case 19:
                return new Card(indecks, 4, 3, 0, 0, 0, 0, 0, 0);
            case 20:
                return new Card(indecks, 4, 4, 0, 0, 0, 0, 0, 0);
            case 21:
                return new Card(indecks, 4, 5, 0, 0, 0, 0, 0, 0);
            case 22:
                return new Card(indecks, 5, 5, 0, 0, 0, 0, 0, 0);
            case 23:
                return new Card(indecks, 6, 5, 0, 0, 0, 0, 0, 0);
            case 24:
                return new Card(indecks, 6, 6, 0, 0, 0, 0, 0, 0);
            case 25:
                return new Card(indecks, 7, 6, 0, 0, 0, 0, 0, 0);
            case 26:
                return new Card(indecks, 0, 0, 1, 0, 0, 0, 0, 0);
            case 27:
                return new Card(indecks, 0, 0, 2, 0, 0, 0, 0, 0);
            case 28:
                return new Card(indecks, 0, 0, 3, 0, 0, 0, 0, 0);
            case 29:
                return new Card(indecks, 0, 0, 4, 0, 0, 0, 0, 0);
            case 30:
                return new Card(indecks, 0, 0, 5, 0, 0, 0, 0, 0);
            case 31:
                return new Card(indecks, 0, 0, 6, 0, 0, 0, 0, 0);
            case 32:
                return new Card(indecks, 0, 0, 7, 0, 0, 0, 0, 0);
            case 33:
                return new Card(indecks, 0, 0, 8, 0, 0, 0, 0, 0);
            case 34:
                return new Card(indecks, 0, 0, 9, 0, 0, 0, 0, 0);
            case 35:
                return new Card(indecks, 0, 0, 10, 0, 0, 0, 0, 0);
            case 36:
                return new Card(indecks, 0, 0, 11, 0, 0, 0, 0, 0);
            case 37:
                return new Card(indecks, 0, 0, 0, 0, 0, 0, 24, 0);
            case 38:
                return new Card(indecks, 0, 0, 13, 0, 0, 0, 0, 0);
            case 39:
                return new Card(indecks, 0, 1, 0, 0, 0, 0, 0, 0);
            case 40:
                return new Card(indecks, 0, 2, 0, 0, 0, 0, 0, 0);
            case 41:
                return new Card(indecks, 0, 3, 0, 0, 0, 0, 0, 0);
            case 42:
                return new Card(indecks, 0, 4, 0, 0, 0, 0, 0, 0);
            case 43:
                return new Card(indecks, 0, 5, 0, 0, 0, 0, 0, 0);
            case 44:
                return new Card(indecks, 0, 6, 0, 0, 0, 0, 0, 0);
            case 45:
                return new Card(indecks, 0, 7, 0, 0, 0, 0, 0, 0);
            case 46:
                return new Card(indecks, 0, 8, 0, 0, 0, 0, 0, 0);
            case 47:
                return new Card(indecks, 0, 9, 0, 0, 0, 0, 0, 0);
            case 48:
                return new Card(indecks, 0, 10, 0, 0, 0, 0, 0, 0);
            case 49:
                return new Card(indecks, 0, 11, 0, 0, 0, 0, 0, 0);
            case 50:
                return new Card(indecks, 0, 12, 0, 0, 0, 0, 0, 0);
            case 51:
                return new Card(indecks, 0, 13, 0, 0, 0, 0, 0, 0);
        }
        return null;
    }
}
