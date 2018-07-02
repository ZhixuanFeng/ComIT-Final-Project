package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardDeck;

public interface CardDeckService
{
    CardDeck getCardDeckById(long id);

    void initializeDeck(CardDeck deck);

    // add a card to the deck, if there's already a card with the same indecks, replace it
    void addCard(Card card, CardDeck deck);

    void removeCard(Card card, CardDeck deck);
}
