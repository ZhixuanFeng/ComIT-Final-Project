package com.fengzhixuan.timoc.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game
{
    private static Map<Integer, Game> games = new HashMap<>();

    private int code;
    private List<Player> players;



    public static Game getGameByCode(int code)
    {
        return games.containsKey(code) ? games.get(code) : null;
    }

    public static Game getGameByCode(String code)
    {
        return getGameByCode(GameCodeGenerator.stringToInt(code));
    }

    public static boolean gameCodeExist(int code)
    {
        return games.containsKey(code);
    }

    public String toString()
    {
        return "Game Session " + GameCodeGenerator.intToString(code) + " - " + code;
    }
}
