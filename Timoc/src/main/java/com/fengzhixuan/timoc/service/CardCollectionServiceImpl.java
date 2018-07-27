package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.repository.CardCollectionRepository;
import com.fengzhixuan.timoc.data.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardCollectionServiceImpl implements CardCollectionService
{
    @Autowired
    private UserService userService;

    @Autowired
    private CardCollectionRepository cardCollectionRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

    @Override
    public CardCollection findById(long id)
    {
        return cardCollectionRepository.findById(id);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addCard(Card card, CardCollection collection, User user)
    {
        collection.getCards().add(card);
        userService.incrementCardCount(user);
        cardService.setCollection(card, collection);
        cardCollectionRepository.save(collection);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void removeCard(Card card, CardCollection collection, User user)
    {
        if (collection.getCards().contains(card))
        {
            collection.getCards().remove(card);
            userService.decrementCardCount(user);
            cardService.setCollection(card, null);
            cardCollectionRepository.save(collection);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void transferCard(Card card, User user_from, User user_to)
    {
        CardCollection collection_from = cardCollectionRepository.findById(user_from.getId());
        CardCollection collection_to = cardCollectionRepository.findById(user_to.getId());

        if (collection_from.getCards().contains(card))
        {
            userService.decrementCardCount(user_from);
            collection_to.getCards().add(card);
            userService.incrementCardCount(user_to);
            cardService.setCollection(card, collection_to);

            cardCollectionRepository.save(collection_from);
            cardCollectionRepository.save(collection_to);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void saveCardCollection(CardCollection collection)
    {
        cardCollectionRepository.save(collection);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public List<Card> getCards(long id)
    {
        return cardRepository.findByCardCollectionId(id);
    }
}
