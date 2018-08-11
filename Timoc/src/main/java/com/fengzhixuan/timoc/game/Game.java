package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.websocket.message.game.GameMessage;
import com.fengzhixuan.timoc.websocket.message.game.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game
{
    // value passed by GameController via gameStart(), used for sending messages to players
    private SimpMessageSendingOperations messagingTemplate;

    private static Map<Integer, Game> games = new HashMap<>();

    private int code;
    private Map<String, Player> players = new HashMap<>(); // username is key
    private Map<Player, Boolean> playerOnlineStatuses = new HashMap<>(); // true means online/connected
    private boolean gameStarted;

    private Game(int code, Map<String, Player> players)
    {
        this.code = code;
        this.players = players;
        // fill in the status map, initialize to all false
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            playerOnlineStatuses.put(playerEntry.getValue(), false);
        }
        gameStarted = false;
    }

    // use this method which calls the constructor to create a game objct
    public static Game createGame(int code, List<Player> playerList)
    {
        Map<String, Player> players = new HashMap<>();
        for (Player player : playerList)
        {
            players.put(player.getName(), player);
        }
        Game game = new Game(code, players);
        games.put(code, game);
        return game;
    }

    // called by GameController, starts the game
    public void gameStart(SimpMessageSendingOperations messagingTemplate)
    {
        gameStarted = true;
        this.messagingTemplate = messagingTemplate;
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            playerEntry.getValue().onGameStart(messagingTemplate);
        }
        messagingTemplate.convertAndSend("/topic/game/" + GameCodeGenerator.intToString(code), new GameMessage(MessageType.GameStart));

        // start first round
        roundStartPhase();
    }

    private void roundStartPhase()
    {
        // deal with all players
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            playerEntry.getValue().onRoundStart();
        }
    }




    public static Game getGameByCode(int code)
    {
        return games.getOrDefault(code, null);
    }

    public static Game getGameByCode(String code)
    {
        return getGameByCode(GameCodeGenerator.stringToInt(code));
    }

    public static boolean gameCodeExist(int code)
    {
        return games.containsKey(code);
    }

    public boolean isPlayerInThisGame(String name)
    {
        return players.containsKey(name);
    }

    public void setPlayerOnlineStatus(String name, boolean status)
    {
        playerOnlineStatuses.put(players.get(name), status);
    }

    public boolean areAllPlayersConnected()
    {
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            if (!playerOnlineStatuses.containsKey(playerEntry.getValue()) || !playerOnlineStatuses.get(playerEntry.getValue()))
            {
                return false;
            }
        }
        return true;
    }

    public boolean isGameStarted()
    {
        return gameStarted;
    }

    public String toString()
    {
        return "Game Session " + GameCodeGenerator.intToString(code) + " - " + code;
    }
}
