package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.repository.CardCollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardCollectionServiceImpl implements CardCollectionService
{
    @Autowired
    private CardCollectionRepository cardCollectionRepository;

    @Autowired
    private CardService cardService;

    @Override
    public void addCard(Card card, CardCollection collection)
    {
        collection.getCards().add(card);
        collection.setCardsOwned(collection.getCardsOwned() + 1);
        cardService.setCollection(card, collection);
        cardCollectionRepository.save(collection);
    }

    @Override
    public void removeCard(Card card, CardCollection collection)
    {
        if (collection.getCards().contains(card))
        {
            collection.getCards().remove(card);
            cardService.setCollection(card, null);
            cardCollectionRepository.save(collection);
        }
    }

    @Override
    public boolean isStorageFull(CardCollection collection)
    {
        return collection.getMaxCardStorage() <= collection.getCardsOwned();
    }

    @Override
    public void saveCardCollection(CardCollection collection)
    {
        cardCollectionRepository.save(collection);
    }
}
