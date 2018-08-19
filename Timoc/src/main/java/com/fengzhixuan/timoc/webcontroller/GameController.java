package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.webcontroller.messagetemplate.DiscardCardMessage;
import com.fengzhixuan.timoc.webcontroller.messagetemplate.PlayCardMessage;
import com.fengzhixuan.timoc.websocket.message.game.GameErrorMessage;
import com.fengzhixuan.timoc.websocket.message.game.GameMessage;
import com.fengzhixuan.timoc.websocket.message.game.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GameController
{
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    /*
     * When a player enter a game, set the player status to online
     */
    @MessageMapping("/game.enter/{code}")
    @SendTo("/topic/game/{code}")
    public GameMessage enterRoom(@DestinationVariable String code, Principal principal)
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

        /*// if game is already started, meaning this player is reconnecting
        if (game.isGameStarted())
        {
            // TODO: reconnect
        }
        // if game is not started yet
        else
        {
            // if all players have joined, start game
            if (game.areAllPlayersConnected())
            {
                game.gameStart(messagingTemplate);
            }
        }*/

        // if all players have joined, start game
        // use this block of code for testing purpose(refresh to restart game), otherwise, use commented code above
        if (game.areAllPlayersConnected())
        {
            game.gameStart(messagingTemplate);
        }


        return new GameMessage(MessageType.EnterSuccessful);
    }

    @MessageMapping("/game.endTurn/{code}")
    @SendTo("/topic/game/{code}")
    public void endTurn(@DestinationVariable String code, Principal principal)
    {
        // check if message is valid, ignore if invalid
        if (principal == null) { return; }
        String username = principal.getName();
        Game game = Game.getGameByCode(code);
        if (game == null) { return; }
        Player player = Player.findPlayerByName(username);
        if (player == null) { return; }
        if (!game.isPlayerInThisGame(username)) { return; }

        game.finishPlayerTurn();
    }

    @MessageMapping("/game.playCard/{code}")
    @SendTo("/topic/game/{code}")
    public void playCard(@DestinationVariable String code, Principal principal, PlayCardMessage message)
    {
        // check if message is valid, ignore if invalid
        if (principal == null) { return; }
        String username = principal.getName();
        Game game = Game.getGameByCode(code);
        if (game == null) { return; }
        Player player = Player.findPlayerByName(username);
        if (player == null) { return; }
        if (!game.isPlayerInThisGame(username)) { return; }

        if (!message.isValid(game, player))
        {
            messagingTemplate.convertAndSendToUser(player.getName(), "/topic/game/" + code, new GameMessage(MessageType.PlayCardFailed));
            return;
        }

        messagingTemplate.convertAndSendToUser(player.getName(), "/topic/game/" + code, new GameMessage(MessageType.PlayCardSuccessful));
        game.playerPlaysCard(player, message);
    }

    @MessageMapping("/game.discardCard/{code}")
    @SendTo("/topic/game/{code}")
    public void discardCard(@DestinationVariable String code, Principal principal, DiscardCardMessage message)
    {
        // check if message is valid, ignore if invalid
        if (principal == null) { return; }
        String username = principal.getName();
        Game game = Game.getGameByCode(code);
        if (game == null) { return; }
        Player player = Player.findPlayerByName(username);
        if (player == null) { return; }
        if (!game.isPlayerInThisGame(username)) { return; }

        if (!message.isValid(game, player))
        {
            messagingTemplate.convertAndSendToUser(player.getName(), "/topic/game/" + code, new GameMessage(MessageType.DiscardCardFailed));
            return;
        }

        messagingTemplate.convertAndSendToUser(player.getName(), "/topic/game/" + code, new GameMessage(MessageType.DiscardCardSuccessful));
        game.playerDiscardsCard(player, message);
    }
}
