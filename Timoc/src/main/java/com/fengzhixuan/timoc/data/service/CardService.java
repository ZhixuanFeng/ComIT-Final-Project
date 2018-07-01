package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;

public interface CardService
{
    void saveCard(Card card);

    void deleteCard(Card card);

    void setCollection(Card card, CardCollection collection);

    Card createCard(int meanQuality, int deviation);
}
