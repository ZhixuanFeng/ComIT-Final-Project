package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.Player;

public interface CardService
{
    void saveCard(Card card);

    void deleteCard(Card card);

    void setOwner(Card card, Player player);

    Card createCard(int meanQuality, int deviation);
}
