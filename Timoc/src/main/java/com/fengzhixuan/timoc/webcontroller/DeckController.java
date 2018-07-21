package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.service.CardDeckService;
import com.fengzhixuan.timoc.service.CardService;
import com.fengzhixuan.timoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class DeckController
{
    @Autowired
    private UserService userService;

    @Autowired
    private CardDeckService cardDeckService;

    @Autowired
    private CardService cardService;

    @RequestMapping(value = "/deck/set_card", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> setCard(@ModelAttribute("id") String idString)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        long userId = user.getId();
        long id;

        // make sure idString is a number
        try
        {
            id = Long.parseLong(idString);
        }
        catch(NumberFormatException nfe)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // id should not be negative
        if (id < 0)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        // if it's starter card, just remove the card with the same indecks from the deck
        else if (id <= 52)
        {
            cardDeckService.removeCardAt((int)id-1, cardDeckService.getCardDeckById(userId));
            return new ResponseEntity<>(HttpStatus.OK);
        }

        // make sure the card exist(did player modify the front end?)
        Card card = cardService.getCardById(id, false);
        if (card == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        CardCollection collection = card.getCardCollection();
        long collectionId = collection.getId();

        // make sure the card belongs to the player and not already in deck(no error or player didn't send false POST info)
        if (collectionId != userId || card.getOwnerTypeEnum() == CardOwnerType.Player_In_Deck)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        else
        {
            cardDeckService.addCard(card, cardDeckService.getCardDeckById(collectionId));
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
