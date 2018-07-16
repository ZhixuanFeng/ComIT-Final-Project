package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardDeck;

import java.util.List;

public interface CardDeckService
{
    CardDeck getCardDeckById(long id);

    void initializeDeck(CardDeck deck);

    // add a card to the deck, if there's already a card with the same indecks, replace it
    void addCard(Card card, CardDeck deck);

    void removeCard(Card card, CardDeck deck);

    void removeCardAt(int indecks, CardDeck deck);

    List<Card> getCards(CardDeck deck);
}
