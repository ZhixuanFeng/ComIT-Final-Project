package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.*;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OfferController
{
    @Autowired
    private UserService userService;

    @Autowired
    private CardDeckService cardDeckService;

    @Autowired
    private CardCollectionService cardCollectionService;

    @Autowired
    private CardService cardService;

    @Autowired
    private OfferService offerService;

    @RequestMapping(value = "/market/new_offer", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> createOffer(@RequestParam("id") String cardIdString,
                                       @RequestParam("price") String priceString)
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
        Card card = cardService.getCardById(id, false);
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

    @RequestMapping(value = "/market/cancel/offer_id", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> cancelOffer(@RequestParam(value = "id") Long id)
    {
        if (id == null) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        long userId = user.getId();

        Offer offer = offerService.findById(id);

        // check if offer exists
        if (offer == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        Player player = offer.getPlayer();

        // check if offer belongs to the user
        if (player.getId() != userId)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        offerService.cancelOffer(offer);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/market/accept/offer_id", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> acceptOffer(@RequestParam(value = "id") Long id)
    {
        if (id == null) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());

        Offer offer = offerService.findById(id);

        // check if offer exists
        if (offer == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        Player buyer = user.getPlayer();
        Player seller = offer.getPlayer();

        // check if buyer's storage is full
        CardCollection collection = buyer.getCardCollection();
        if (cardCollectionService.isStorageFull(collection))
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // check if buyer has enough gold
        int price = offer.getPrice();
        if (buyer.getGold() < price)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        offerService.acceptOffer(offer, buyer, seller);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
