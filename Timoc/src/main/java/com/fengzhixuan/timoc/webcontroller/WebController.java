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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Random;

@Controller
public class WebController
{
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
    @Autowired
    private UserService userService;
    @Autowired
    private CardService cardService;
    @Autowired
    private CardCollectionService cardCollectionService;
    @Autowired
    private CardDeckService cardDeckService;
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
    public ModelAndView randomCard()
    {
        Random random = new Random();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("deck");
        Card card = cardService.createCard(0, 10);
        modelAndView.addObject(card);
        return modelAndView;
    }
}
