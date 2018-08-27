package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardDeck;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.service.CardCollectionService;
import com.fengzhixuan.timoc.service.CardDeckService;
import com.fengzhixuan.timoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardRESTController
{
    @Autowired
    private UserService userService;

    @Autowired
    private CardCollectionService cardCollectionService;

    @Autowired
    private CardDeckService cardDeckService;

    // get a user's whole collection of cards
    @RequestMapping(value="/rest/cards", method=RequestMethod.GET)
    private List<Card> getCards()
    {
        // get user info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());

        return cardCollectionService.getCards(user.getId());
    }

    // get a user's deck of cards
    @RequestMapping(value="/deck/cards", method=RequestMethod.POST)
    private List<Card> getDeck()
    {
        // get user info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());

        return cardDeckService.getCards(cardDeckService.getCardDeckById(user.getId()));
    }
}
