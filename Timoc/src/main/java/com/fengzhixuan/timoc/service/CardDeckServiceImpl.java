package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardDeck;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.data.repository.CardDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CardDeckServiceImpl implements CardDeckService
{
    @Autowired
    private CardDeckRepository cardDeckRepository;

    @Autowired
    private CardService cardService;

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public CardDeck getCardDeckById(long id)
    {
        return cardDeckRepository.findById(id);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void initializeDeck(CardDeck deck)
    {
        cardDeckRepository.save(deck);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addCard(Card card, CardDeck deck)
    {
        // if adding a non-starter card into deck
        if (card.getOwnerTypeEnum() == CardOwnerType.Player_Not_In_Deck)
        {
            int indecks = card.getIndecks();

            // if there is a non-starter card in deck, take it out
            if (deck.getCards().containsKey(indecks))
            {
                // set deck attribute of the card to be null, setDeck will set the new ownerType
                cardService.setDeck(deck.getCards().get(indecks), null);
            }

            // put new card into deck
            cardService.setDeck(card, deck);
            deck.getCards().replace(indecks, card);
            cardDeckRepository.save(deck);
        } else if (card.getOwnerTypeEnum() == CardOwnerType.AllPlayers)
        {
            int indecks = card.getIndecks();
            cardService.setDeck(deck.getCards().get(indecks), null); // saves card
            cardDeckRepository.save(deck);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void removeCard(Card card, CardDeck deck)
    {
        int indecks = card.getIndecks();

        // make sure the card is in the deck, then remove it
        if (card.getOwnerTypeEnum() == CardOwnerType.Player_In_Deck && deck.getCards().get(indecks) == card)
        {
            cardService.setDeck(card, null);
            deck.getCards().remove(indecks);
        }
        cardDeckRepository.save(deck);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void removeCardAt(int indecks, CardDeck deck)
    {
        Map<Integer, Card> cards = deck.getCards();
        Card card = cards.get(indecks);

        if (card != null)
        {
            cardService.setDeck(card, null);
            cards.remove(indecks);
            cardDeckRepository.save(deck);
        }
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public List<Card> getCards(CardDeck deck)
    {
        List<Card> cards = new ArrayList<>();
        Map<Integer, Card> map = deck.getCards();
        for (int i = 0; i < 52; i++)
        {
            if (map.containsKey(i))
            {
                cards.add(map.get(i));
            }
            else
            {
                cards.add(cardService.getStarterCard(i));
            }
        }
        return cards;
    }

    // turn card object entities to card objects to be stored in memory
    // TODO: could be a bad idea to have two classes with the same name "Card", do something?
    @Override
    public Map<Integer, com.fengzhixuan.timoc.game.Card> getCardsFromCardEntities(CardDeck deck)
    {
        Map<Integer, com.fengzhixuan.timoc.game.Card> cards = new HashMap<>();
        Map<Integer, com.fengzhixuan.timoc.data.entity.Card> entities = deck.getCards();
        entities.forEach((key, value) -> {
            cards.put(key, com.fengzhixuan.timoc.game.Card.cardEntityToCard(value));
        });

        return cards;
    }
}
