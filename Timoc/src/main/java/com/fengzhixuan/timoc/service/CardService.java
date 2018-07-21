package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.CardDeck;
import com.fengzhixuan.timoc.data.entity.Player;

public interface CardService
{
    Card getCardById(long id, boolean inCache);

    void saveCard(Card card);

    void deleteCard(Card card);

    void setCollection(Card card, CardCollection collection);

    void setDeck(Card card, CardDeck deck);

    Card createCard(int meanQuality, int deviation);

    Card getStarterCard(int indecks);

    void turnIntoGold(Card card, Player player);
}
