package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardDeck;

public interface CardDeckService
{
    void initializeDeck(CardDeck deck);

    void addCard(Card card, CardDeck deck);

    void removeCard(Card card, CardDeck deck);
}
