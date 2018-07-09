package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.repository.CardCollectionRepository;
import com.fengzhixuan.timoc.data.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardCollectionServiceImpl implements CardCollectionService
{
    @Autowired
    private CardCollectionRepository cardCollectionRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

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
            collection.setCardsOwned(collection.getCardsOwned() - 1);
            cardService.setCollection(card, null);
            cardCollectionRepository.save(collection);
        }
    }

    @Override
    public void transferCard(Card card, CardCollection collection_from, CardCollection collection_to)
    {
        if (collection_from.getCards().contains(card))
        {
            collection_from.getCards().remove(card);
            collection_from.setCardsOwned(collection_from.getCardsOwned() - 1);

            collection_to.getCards().add(card);
            collection_to.setCardsOwned(collection_to.getCardsOwned() + 1);

            cardService.setCollection(card, collection_to);
            cardCollectionRepository.save(collection_from);
            cardCollectionRepository.save(collection_to);
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

    @Override
    public List<Card> getCards(long id)
    {
        return cardRepository.findByCardCollectionId(id);
    }
}
