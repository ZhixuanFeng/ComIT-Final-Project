package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.Player;

public interface CardService
{
    public void saveCard(Card card);

    public void deleteCard(Card card);

    public void setOwner(Card card, Player player);
}
