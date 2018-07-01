package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import org.springframework.stereotype.Service;

@Service
public interface CardCollectionService
{
    void addCard(Card card, CardCollection collection);

    void removeCard(Card card, CardCollection collection);

    boolean isStorageFull(CardCollection collection);

    void saveCardCollection(CardCollection collection);
}
