package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.Player;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
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
public class CardController
{
    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/card/turn_into_gold", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> turnCardIntoGold(@RequestParam(value = "id") Long id)
    {
        if (id == null) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        Player player = user.getPlayer();
        long playerId = player.getId();

        // make sure it's not a starter card
        if (id <= 52)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // make sure the card exists
        Card card = cardService.getCardById(id);
        if (card == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // make sure the card ownerType is applicable
        if (card.getOwnerTypeEnum() != CardOwnerType.Player_Not_In_Deck && card.getOwnerTypeEnum() != CardOwnerType.Player_In_Deck)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        // make sure the card belongs to the player
        CardCollection collection = card.getCardCollection();
        if (collection.getId() != playerId)
        {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        cardService.turnIntoGold(card, player);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
