package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.game.Enemy;
import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class DisplayController
{
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/display.enter/{code}")
    @SendTo("/topic/display/{code}")
    public List<GameMessage> startDisplay(@DestinationVariable String code)
    {
        Game game = Game.getGameByCode(code);
        if (game == null) { return null; }

        game.setDisplayConnected();

        if (game.isGameStarted())
        {
            // send game, player, enemy information to display
            List<GameMessage> messages = new ArrayList<>();
            messages.add(new GameInfoMessage(game));
            messages.add(new GamePlayerInfoMessage(MessageType.PlayerInfo, game.getPlayers().values().toArray(new Player[0])));
            messages.add(new GameEnemyInfoMessage(MessageType.EnemyInfo, game.getAliveEnemies()));
            for (Map.Entry<String, Player> playerEntry : game.getPlayers().entrySet())
            {
                messages.add(new GameDeckMessage(MessageType.PlayerDeck, playerEntry.getValue().getId(), playerEntry.getValue().getDeck()));
            }

            Player currentPlayer = game.getCurrentPlayer();
            messages.add(new GamePlayerMessage(MessageType.PlayerStartsTurn, currentPlayer.getId()));
            messages.add(new GameCardsMessage(MessageType.Hand, currentPlayer.getHandIndeckses()));
            messages.add(new DisplayStateMessage(game.getDisplayStates()));
            return messages;
        }

        // if all players are connected and a display is setup, start game
        else if (game.areAllPlayersConnected())
        {
            game.gameStart(messagingTemplate);
        }

        return null;
    }
}
