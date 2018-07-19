package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.*;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.service.CardDeckService;
import com.fengzhixuan.timoc.service.CardService;
import com.fengzhixuan.timoc.service.OfferService;
import com.fengzhixuan.timoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OfferController
{
    @Autowired
    private UserService userService;

    @Autowired
    private CardDeckService cardDeckService;

    @Autowired
    private CardService cardService;

    @Autowired
    private OfferService offerService;

    @RequestMapping(value = "/market/new_offer", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> createOffer(@ModelAttribute("id") String cardIdString,
                                       @ModelAttribute("price") String priceString)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        Player player = user.getPlayer();
        long userId = user.getId();
        long id;
        int price;

        // make sure idString is a number
        try
        {
            id = Long.parseLong(cardIdString);
        }
        catch(NumberFormatException nfe)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // make sure the card exist(did player modify the front end?)
        Card card = cardService.getCardById(id);
        if (card == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        CardCollection collection = card.getCardCollection();
        long collectionId = collection.getId();

        // make sure the card belongs to the player
        if (collectionId != userId)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // make sure the card is sell-able
        if (card.getOwnerTypeEnum() != CardOwnerType.Player_In_Deck && card.getOwnerTypeEnum() != CardOwnerType.Player_Not_In_Deck)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // at this point, there is no problem for the card id
        // check if the price is a string
        try
        {
            price = Integer.parseInt(priceString);
        }
        catch(NumberFormatException nfe)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // check if the price is in range
        if (price <= 0 || price > 9999)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // both card id and price is ok, create offer
        Offer offer = offerService.createOffer(player, card, price);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
