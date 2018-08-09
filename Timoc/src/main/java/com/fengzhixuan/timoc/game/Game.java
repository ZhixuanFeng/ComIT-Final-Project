package com.fengzhixuan.timoc.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game
{
    private static Map<Integer, Game> games = new HashMap<>();

    private int code;
    private Map<String, Player> players = new HashMap<>(); // username is key
    private Map<Player, Boolean> playerOnlineStatuses = new HashMap<>(); // true means online/connected

    private Game(int code, Map<String, Player> players)
    {
        this.code = code;
        this.players = players;
        // fill in the status map, initialize to all false
        for (Map.Entry<String, Player> playerEntry : players.entrySet())
        {
            playerOnlineStatuses.put(playerEntry.getValue(), false);
        }
    }

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

    public String toString()
    {
        return "Game Session " + GameCodeGenerator.intToString(code) + " - " + code;
    }
}
