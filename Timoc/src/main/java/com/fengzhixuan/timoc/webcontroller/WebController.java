package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.*;
import com.fengzhixuan.timoc.service.CardCollectionService;
import com.fengzhixuan.timoc.service.CardDeckService;
import com.fengzhixuan.timoc.service.CardService;
import com.fengzhixuan.timoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController
{
    @Autowired
    private UserService userService;
    @Autowired
    private CardService cardService;
    @Autowired
    private CardCollectionService cardCollectionService;
    @Autowired
    private CardDeckService cardDeckService;

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    public ModelAndView home()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }

    /*
     *  URL for testing card creation
     */
    @RequestMapping(value = "/getCard", method = RequestMethod.GET)
    public ModelAndView getCard()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        CardCollection cardCollection = cardCollectionService.findById(user.getId());
        if (!userService.isStorageFull(user))
        {
            Card newCard = cardService.createCard(user.getLevel(), 10);
            cardCollectionService.addCard(newCard, cardCollection, user);
            cardDeckService.addCard(newCard, cardDeckService.getCardDeckById(user.getId())); // test deck
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("hello");
        return modelAndView;
    }

    @RequestMapping(value = "/deck", method = RequestMethod.GET)
    public ModelAndView viewDeck()
    {
//        // get user info
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = userService.findUserByUsername(auth.getName());
//
//        // get deck and allCards
//        CardDeck deck = cardDeckService.getCardDeckById(user.getId());
//        List<Card> cards = cardDeckService.getCards(deck);
//        //List<Card> allCards = cardRepository.findByCardDeckId(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("deck");
//        modelAndView.addObject("cards", cards);
        return modelAndView;
    }

    @RequestMapping(value = "/collection", method = RequestMethod.GET)
    public ModelAndView viewCollection()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("collection");
        return modelAndView;
    }

    @RequestMapping(value = "/market", method = RequestMethod.GET)
    public ModelAndView viewMarket()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("market");
        return modelAndView;
    }

    @RequestMapping(value = "/room", method = RequestMethod.GET)
    public ModelAndView viewRoom()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("room");
        modelAndView.addObject("maxlength", 4);
        return modelAndView;
    }
}
