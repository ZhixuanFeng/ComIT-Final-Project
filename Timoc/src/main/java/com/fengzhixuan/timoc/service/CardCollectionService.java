package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CardCollectionService
{
    CardCollection findById(long id);

    void addCard(Card card, CardCollection collection, User user);

    void removeCard(Card card, CardCollection collection, User user);

    void transferCard(Card card, User user_from, User user_to);

    void saveCardCollection(CardCollection collection);

    List<Card> getCards(long id);
}
