package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import org.springframework.stereotype.Service;

@Service
public interface CardCollectionService
{
    void addCard(Card card, CardCollection collection);

    void removeCard(Card card, CardCollection collection);

    void transferCard(Card card, CardCollection collection_from, CardCollection collection_to);

    boolean isStorageFull(CardCollection collection);

    void saveCardCollection(CardCollection collection);
}
