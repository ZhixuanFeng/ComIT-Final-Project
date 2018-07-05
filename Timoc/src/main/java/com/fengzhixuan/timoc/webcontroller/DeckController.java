package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardDeck;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.repository.CardRepository;
import com.fengzhixuan.timoc.service.CardDeckService;
import com.fengzhixuan.timoc.service.CardService;
import com.fengzhixuan.timoc.service.PlayerService;
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
@RequestMapping(value = "/deck")
public class DeckController
{
    @Autowired
    private UserService userService;

    @Autowired
    private CardDeckService cardDeckService;

    @Autowired
    private CardRepository cardRepository;

    /*
     * Testing deck.html
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView randomCard()
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
