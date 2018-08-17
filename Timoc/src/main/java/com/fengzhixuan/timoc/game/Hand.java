package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.game.enums.PokerHand;

public class Hand
{
    // find out what kind of hand it is for the passed in cards
    public static PokerHand identifyHand(Card[] cards)
    {
        if (cards.length == 2)
        {
            if (cards[0].getRank() == cards[1].getRank())
            {
                return PokerHand.OnePair;
            }
        }
        else if (cards.length == 3)
        {
            cards = sortCards(cards);
            if (cards[0].getRank() == cards[1].getRank() && cards[1].getRank() == cards[2].getRank())
            {
                return PokerHand.ThreeOfAKind;
            }
            else if (cards[0].getRank() == cards[1].getRank() || cards[1].getRank() == cards[2].getRank())
            {
                return PokerHand.OnePair;
            }
        }
        else if (cards.length == 4)
        {
            cards = sortCards(cards);
            if (cards[0].getRank() == cards[1].getRank() && cards[1].getRank() == cards[2].getRank() && cards[2].getRank() == cards[3].getRank())
            {
                return PokerHand.FourOfAKind;
            }
            else if (cards[0].getRank() == cards[1].getRank() && cards[1].getRank() == cards[2].getRank() || cards[1].getRank() == cards[2].getRank() && cards[2].getRank() == cards[3].getRank())
            {
                return PokerHand.ThreeOfAKind;
            }
            else if (cards[0].getRank() == cards[1].getRank() && cards[2].getRank() == cards[3].getRank())
            {
                return PokerHand.TwoPair;
            }
            else if (cards[0].getRank() == cards[1].getRank() || cards[1].getRank() == cards[2].getRank() || cards[2].getRank() == cards[3].getRank())
            {
                return PokerHand.OnePair;
            }
        }
        else if (cards.length == 5)
        {
            cards = sortCards(cards);
            boolean isPair = isPair(cards);
            boolean isTwoPair = isTwoPair(cards);
            boolean isThreeOfAKind = isThreeOfAKind(cards);
            boolean isStraight = isStraight(cards);
            boolean isFlush = isFlush(cards);
            boolean isFullHouse = isFullHouse(cards);
            boolean isFourOfAKind = isFourOfAKind(cards);
            boolean isRoyal = isRoyal(cards);

            if (isStraight && isFlush)
            {
                return isRoyal ? PokerHand.RoyalFlush : PokerHand.StraightFlush;
            }
            else if (isFourOfAKind)
            {
                return PokerHand.FourOfAKind;
            }
            else if (isFullHouse)
            {
                return PokerHand.FullHouse;
            }
            else if (isFlush)
            {
                return PokerHand.Flush;
            }
            else if (isStraight)
            {
                return PokerHand.Straight;
            }
            else if (isThreeOfAKind)
            {
                return PokerHand.ThreeOfAKind;
            }
            else if (isTwoPair)
            {
                return PokerHand.TwoPair;
            }
            else if (isPair)
            {
                return PokerHand.OnePair;
            }
        }

        return PokerHand.HighCard;
    }

    // sort cards in increasing order based on rank, if rank is equal, sort by suit
    public static Card[] sortCards(Card[] cards)
    {
        if (cards.length == 1) { return cards; }

        for (int i = 0; i < cards.length; i++)
        {
            for (int j = 1; j < cards.length - i; j++)
            {
                if (cards[j - 1].getRank() > cards[j].getRank() || (cards[j - 1].getRank() == cards[j].getRank() && cards[j - 1].getSuit() > cards[j].getSuit()))
                {
                    Card c = cards[j - 1];
                    cards[j - 1] = cards[j];
                    cards[j] = c;
                }
            }
        }
        return cards;
    }

    // result is also true when the cards is two pair, three of a kind, full house or four of a kind
    public static boolean isPair(Card[] cards)
    {
        return cards[0].getRank() == cards[1].getRank() ||
                cards[1].getRank() == cards[2].getRank() ||
                cards[2].getRank() == cards[3].getRank() ||
                cards[3].getRank() == cards[4].getRank();
    }

    // result is also true when the cards is full house or four of a kind
    public static boolean isTwoPair(Card[] cards)
    {
        return (cards[0].getRank() == cards[1].getRank() && (cards[2].getRank() == cards[3].getRank() || cards[3].getRank() == cards[4].getRank())) ||
                (cards[1].getRank() == cards[2].getRank() && cards[3].getRank() == cards[4].getRank());
    }

    // result is also true when the cards is full house or four of a kind
    public static boolean isThreeOfAKind(Card[] cards)
    {
        return (cards[0].getRank() == cards[1].getRank() && cards[0].getRank() == cards[2].getRank()) ||
                (cards[1].getRank() == cards[2].getRank() && cards[1].getRank() == cards[3].getRank()) ||
                (cards[2].getRank() == cards[3].getRank() && cards[2].getRank() == cards[4].getRank());
    }

    // result is also true when the cards is straight flush or royal flush
    public static boolean isStraight(Card[] cards)
    {
        int i = 0;
        for (i = 0; i < 4 && cards[i].getRank() == cards[i+1].getRank() - 1 ; i++);
        return i == 4;
    }

    // result is also true when the cards is straight flush or royal flush
    public static boolean isFlush(Card[] cards)
    {
        int i = 0;
        for (i = 0; i < 4 && cards[i].getSuit() == cards[i+1].getSuit() ; i++);
        return i == 4;
    }

    public static boolean isFullHouse(Card[] cards)
    {
        return (cards[0].getRank() == cards[1].getRank() && cards[0].getRank() == cards[2].getRank() && cards[3].getRank() == cards[4].getRank()) ||
                (cards[0].getRank() == cards[1].getRank() && cards[2].getRank() == cards[3].getRank() && cards[2].getRank() == cards[4].getRank());
    }

    public static boolean isFourOfAKind(Card[] cards)
    {
        return (cards[0].getRank() == cards[1].getRank() && cards[0].getRank() == cards[2].getRank() && cards[0].getRank() == cards[3].getRank()) ||
                (cards[1].getRank() == cards[2].getRank() && cards[1].getRank() == cards[3].getRank() && cards[1].getRank() == cards[4].getRank());
    }

    public static boolean isRoyal(Card[] cards)
    {
        return cards[0].getRank() == 1 && cards[1].getRank() == 10 && cards[2].getRank() == 11 && cards[3].getRank() == 12 && cards[4].getRank() == 13;
    }
}
