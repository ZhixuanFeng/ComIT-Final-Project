package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.CardDeck;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.game.Card;
import com.fengzhixuan.timoc.game.GameCodeGenerator;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.game.Room;
import com.fengzhixuan.timoc.service.CardDeckService;
import com.fengzhixuan.timoc.service.UserService;
import com.fengzhixuan.timoc.websocket.message.room.RoomInfoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class RoomController
{
    @Autowired
    private UserService userService;

    @Autowired
    private CardDeckService cardDeckService;

    @RequestMapping(value = "/room/create", method = RequestMethod.POST)
    public @ResponseBody
    String createRoom()
    {
        Room room = Room.createRoom();
        return room.getCodeString();
    }

    @RequestMapping(value = "/room/enter", method = RequestMethod.POST)
    public @ResponseBody
    String enterRoom(@RequestParam String code)
    {
        // code invalid
        if (!GameCodeGenerator.isCodeValid(code)) { return "Invalid code"; }

        Room room = Room.getRoomByCode(code);

        // room not exist
        if (room == null) { return "Invalid code"; }

        // room full
        if (room.isFull()) { return "Room full"; }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        long userId = user.getId();
        CardDeck deck = cardDeckService.getCardDeckById(userId);

        // create player object and add into room
        Player player = new Player(user.getUsername(), room.getCode(), Card.cardEntitiesToCards(deck.getCards()));
        room.addPlayer(player);

        return "OK";
    }

    @MessageMapping("/room.enter/{code}")
    @SendTo("/topic/room/{code}")
    public RoomInfoMessage enterRoom(@DestinationVariable String code, Principal principal)
    {
        Room room = Room.getRoomByCode(code);
        if (room == null)
        {
            // shouldn't happen as StompSubscribeEventHandler already checked this
            return null;
        }
        return RoomInfoMessage.createMessage(room.getPlayers());
    }
}