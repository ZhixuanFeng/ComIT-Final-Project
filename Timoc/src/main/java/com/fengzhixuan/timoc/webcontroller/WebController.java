package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.*;
import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.GameCodeGenerator;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.game.Room;
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
import org.springframework.web.bind.annotation.RequestParam;
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
        modelAndView.setViewName("join");
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

    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public ModelAndView getJoinPage()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("join");
        modelAndView.addObject("maxcodelength", 4);
        return modelAndView;
    }

    @RequestMapping(value = "/room", method = RequestMethod.GET)
    public ModelAndView enterRoom(@RequestParam("code") String code)
    {
        ModelAndView modelAndView = new ModelAndView();
        code = code.toUpperCase();
        Room room = Room.getRoomByCode(code);
        if (GameCodeGenerator.isCodeValid(code) && room != null && !room.isFull())
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByUsername(auth.getName());
            long userId = user.getId();
            CardDeck deck = cardDeckService.getCardDeckById(userId);

            // create player object and add into room
            Player player = new Player(user.getUsername(), code, com.fengzhixuan.timoc.game.Card.cardEntitiesToCards(deck.getCards()));
            room.addPlayer(player);
            modelAndView.setViewName("room");
        }
        else
        {
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/controller", method = RequestMethod.GET)
    public ModelAndView enterController(@RequestParam("code") String code)
    {
        ModelAndView modelAndView = new ModelAndView();
        if (GameCodeGenerator.isCodeValid(code.toUpperCase()) && Game.gameCodeExist(code.toUpperCase()))
        {
            modelAndView.setViewName("controller");
        }
        else
        {
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/display", method = RequestMethod.GET)
    public ModelAndView watchGame(@RequestParam(value = "code", required = false) String code)
    {
        ModelAndView modelAndView = new ModelAndView();
        if (code == null)
        {
            modelAndView.setViewName("display_entercode");
        }
        else if (GameCodeGenerator.isCodeValid(code.toUpperCase()) && Game.gameCodeExist(code.toUpperCase()))
        {
            modelAndView.setViewName("display");
        }
        else
        {
            modelAndView.setViewName("display_entercode");
            modelAndView.addObject("errorMessage", "Invalid code");
        }
        return modelAndView;
    }
}
