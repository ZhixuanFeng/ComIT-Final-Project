package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardDeck;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.data.repository.CardDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardDeckServiceImpl implements CardDeckService
{
    @Autowired
    private CardDeckRepository cardDeckRepository;

    @Autowired
    private CardService cardService;

    @Override
    public void initializeDeck(CardDeck deck)
    {
        CardDeck startDeck = cardDeckRepository.getOne(1L);
        deck.setCards(startDeck.getCards());
        cardDeckRepository.save(deck);
    }

    @Override
    public void addCard(Card card, CardDeck deck)
    {
        int index = (card.getSuit() + 1) * (card.getRank() - 1);
        Card cardToSwitchOut = deck.getCards()[index];

        if (cardToSwitchOut.getOwnerTypeEnum() == CardOwnerType.Player)
        {
            cardService.setDeck(cardToSwitchOut, null);
        }

        if (card.getOwnerTypeEnum() == CardOwnerType.Player)
        {
            cardService.setDeck(card, deck);
        }

        deck.getCards()[index] = card;
        cardDeckRepository.save(deck);
    }

    @Override
    public void removeCard(Card card, CardDeck deck)
    {
        int index = (card.getSuit() + 1) * (card.getRank() - 1);
        if (card.getOwnerTypeEnum() == CardOwnerType.Player && deck.getCards()[index] == card)
        {
            cardService.setDeck(card, null);
        }

        Card starterCard = cardService.getCardById(index + 1);
        deck.getCards()[index] = starterCard;
        cardDeckRepository.save(deck);
    }
}
