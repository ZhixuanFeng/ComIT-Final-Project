package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.Player;
import com.fengzhixuan.timoc.data.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements CardService
{
    @Autowired
    private CardRepository cardRepository;

    @Override
    public void saveCard(Card card)
    {
        cardRepository.save(card);
    }

    @Override
    public void deleteCard(Card card)
    {
        cardRepository.delete(card);
    }

    @Override
    public void setOwner(Card card, Player player)
    {
        card.setPlayer(player);
        cardRepository.save(card);
    }
}
