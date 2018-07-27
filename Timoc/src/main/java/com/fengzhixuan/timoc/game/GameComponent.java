package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GameComponent
{
    @Autowired
    private CardService cardService;

    @EventListener(ApplicationReadyEvent.class)
    public void init()
    {
        loadStarterCards();
    }

    private void loadStarterCards()
    {
        Card[] starters = new Card[52];
        for (int i = 0; i < 52; i++)
        {
            starters[i] = Card.cardEntityToCard(cardService.getStarterCard(i));
        }
        Player.setStarters(starters);
    }
}
