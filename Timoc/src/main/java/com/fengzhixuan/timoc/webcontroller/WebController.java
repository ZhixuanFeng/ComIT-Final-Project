package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.Player;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.service.CardCollectionService;
import com.fengzhixuan.timoc.data.service.CardService;
import com.fengzhixuan.timoc.data.service.UserService;
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
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("hello");
        return modelAndView;
    }
}
