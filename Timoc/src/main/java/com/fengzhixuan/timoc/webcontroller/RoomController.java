package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.CardDeck;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.game.*;
import com.fengzhixuan.timoc.service.CardDeckService;
import com.fengzhixuan.timoc.service.UserService;
import com.fengzhixuan.timoc.websocket.message.room.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class RoomController
{
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

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

    /*
     * Handle post request to create player object and put it in the room object
     * Could have done it in the messageMapping but it does not have JPA access
     */
    @RequestMapping(value = "/room/enter", method = RequestMethod.POST)
    public @ResponseBody
    String tryCode(@RequestParam String code)
    {
        // code invalid
        if (!GameCodeGenerator.isCodeValid(code)) { return "Invalid code"; }

        Room room = Room.getRoomByCode(code);

        // room not exist
        if (room == null) { return "Wrong code"; }

        // room full
        if (room.isFull()) { return "Room full"; }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        long userId = user.getId();
        CardDeck deck = cardDeckService.getCardDeckById(userId);

        // create player object and add into room
        Player player = new Player(user.getUsername(), code, Card.cardEntitiesToCards(deck.getCards()));
        room.addPlayer(player);

        return "OK";
    }

    /*
     * When a player enter a room, broadcast the info of players in the room to everyone inside
     */
    @MessageMapping("/room.enter/{code}")
    @SendTo("/topic/room/{code}")
    public RoomNewPlayerMessage enterRoom(@DestinationVariable String code, Principal principal)
    {
        if (principal == null) { return null; }
        Room room = Room.getRoomByCode(code);
        if (room == null) { return null; }
        RoomInfoMessage roomInfoMessage = new RoomInfoMessage(room.getAllPlayerInfo());
        messagingTemplate.convertAndSendToUser(principal.getName(), "/topic/room/" + code, roomInfoMessage);

        return new RoomNewPlayerMessage(room.getPlayerInfo(principal.getName()));
    }

    /*
     * When a player clicks the ready button, toggle their ready status and tell everyone
     */
    @MessageMapping("/room.ready/{code}")
    @SendTo("/topic/room/{code}")
    public RoomReadyMessage toggleReady(@DestinationVariable String code, Principal principal)
    {
        if (principal == null) { return null; }
        Room room = Room.getRoomByCode(code);
        if (room == null) { return null; }
        String username = principal.getName();
        Player player = Player.findPlayerByName(username);

        boolean currentIsReady = room.isPlayerReady(player);
        room.setPlayerReady(player, !currentIsReady); // toggle ready status

        if (room.areAllPlayersReady())
            messagingTemplate.convertAndSend("/topic/room/" + code, new RoomMessage(MessageType.AllReady));
        return new RoomReadyMessage(MessageType.Ready, username, !currentIsReady);
    }

    @MessageMapping("/room.start/{code}")
    @SendTo("/topic/room/{code}")
    public RoomStartGameMessage createGame(@DestinationVariable String code, Principal principal)
    {
        if (principal == null) { return null; }
        Room room = Room.getRoomByCode(code);
        int codeInt = GameCodeGenerator.stringToInt(code);
        if (room == null)
        {
            // if game is already created, room should be null
            // check if the game is already created and the player belongs in the game
            if (Game.gameCodeExist(codeInt) && Game.getGameByCode(codeInt).isPlayerInThisGame(principal.getName()))
            {
                return new RoomStartGameMessage();
            }
            else
            {
                return null;
            }
        }
        if (room.areAllPlayersReady())
        {
            // TODO: check if created game is started in 10 mins, remove if not started
            Game game = Game.createGame(codeInt, room.getPlayers());
            Room.removeRoom(codeInt);
            return new RoomStartGameMessage();
        }

        return null;
    }
}