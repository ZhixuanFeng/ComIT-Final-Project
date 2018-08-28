package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ControllerController
{
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/controller.enter/{code}")
    @SendTo("/topic/controller/{code}")
    public GameMessage startDisplay(@DestinationVariable String code, Principal principal)
    {
        if (principal == null) { return new GameErrorMessage("User not found"); }
        String username = principal.getName();

        Game game = Game.getGameByCode(code);
        if (game == null) { return new GameErrorMessage("Game not found"); }

        Player player = Player.findPlayerByName(username);
        if (player == null) { return new GameErrorMessage("Player not found"); }

        if (!game.isPlayerInThisGame(username)) { return new GameErrorMessage("Error joining this game"); }

        // set status
        game.setPlayerOnlineStatus(username, true);

        // if game is already started, meaning this player is reconnecting
        if (game.isGameStarted())
        {
            // TODO: reconnect
        }
        // if game is not started yet
        else
        {
            // if all players are connected and a display is setup, start game
            if (game.isDisplayConnected() && game.areAllPlayersConnected())
            {
                game.gameStart(messagingTemplate);
            }
        }


        return new GameMessage(MessageType.EnterSuccessful);
    }

    @MessageMapping("/controller/{code}")
    @SendTo("/topic/display/{code}")
    public int buttonPress(@DestinationVariable String code, int buttonCode)
    {
        Game game = Game.getGameByCode(code);
        if (game == null) { return -1; }

        return buttonCode;
    }
}
