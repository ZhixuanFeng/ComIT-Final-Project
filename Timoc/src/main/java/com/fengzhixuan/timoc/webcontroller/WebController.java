package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.*;
import com.fengzhixuan.timoc.data.repository.CardRepository;
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

import java.util.List;

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
        Player player = user.getPlayer();
        CardCollection cardCollection = player.getCardCollection();
        if (!cardCollectionService.isStorageFull(cardCollection))
        {
            Card newCard = cardService.createCard(player.getLevel(), 10);
            cardCollectionService.addCard(newCard, player.getCardCollection());
            cardDeckService.addCard(newCard, cardDeckService.getCardDeckById(player.getId())); // test deck
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("hello");
        return modelAndView;
    }

    /*
     * Testing deck.html
     */
    @RequestMapping(value = "/deck", method = RequestMethod.GET)
    public ModelAndView viewDeck()
    {
        // get user info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());

        // get deck and cards
        CardDeck deck = cardDeckService.getCardDeckById(user.getId());
        List<Card> cards = cardDeckService.getCards(deck);
        //List<Card> cards = cardRepository.findByCardDeckId(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("deck");
        modelAndView.addObject("cards", cards);
        return modelAndView;
    }
}
