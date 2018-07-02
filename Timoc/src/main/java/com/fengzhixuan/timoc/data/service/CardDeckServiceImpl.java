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
    public CardDeck getCardDeckById(long id)
    {
        return cardDeckRepository.getOne(id);
    }

    @Override
    public void initializeDeck(CardDeck deck)
    {
        cardDeckRepository.save(deck);
    }

    @Override
    public void addCard(Card card, CardDeck deck)
    {
        int indecks = card.getIndecks();
        if (deck.getCards().containsKey(indecks))
        {
            cardService.setDeck(deck.getCards().get(indecks), null);
        }
        cardService.setDeck(card, deck);
        deck.getCards().replace(indecks, card);
        cardDeckRepository.save(deck);
    }

    @Override
    public void removeCard(Card card, CardDeck deck)
    {
        int indecks = card.getIndecks();
        if (card.getOwnerTypeEnum() == CardOwnerType.Player && deck.getCards().get(indecks) == card)
        {
            cardService.setDeck(card, null);
            deck.getCards().remove(indecks);
        }
        cardDeckRepository.save(deck);
    }
}
