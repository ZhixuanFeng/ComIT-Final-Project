package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.CardDeck;

public interface CardService
{
    Card getCardById(long id);

    void saveCard(Card card);

    void deleteCard(Card card);

    void setCollection(Card card, CardCollection collection);

    void setDeck(Card card, CardDeck deck);

    Card createCard(int meanQuality, int deviation);
}
