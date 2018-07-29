package com.fengzhixuan.timoc.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room
{
    private static Map<Integer, Room> rooms = new HashMap<>();

    private int code;
    private List<Player> players = new ArrayList<>();
    private Map<Player, PlayerStatus> playerStatuses = new HashMap<>();

    public Room(int code)
    {
        this.code = code;
    }

    public static Room createRoom()
    {
        int code = GameCodeGenerator.getNextCodeInt();
        Room room = new Room(code);
        rooms.put(code, room);
        return room;
    }

    public static Room createRoom(int code)
    {
        Room room = new Room(code);
        rooms.put(code, room);
        return room;
    }

    public static Map<Integer, Room> getRooms()
    {
        return rooms;
    }

    public int getCode()
    {
        return code;
    }

    public String getCodeString()
    {
        return GameCodeGenerator.intToString(code);
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public static Room getRoomByCode(int code)
    {
        return rooms.containsKey(code) ? rooms.get(code) : null;
    }

    public static Room getRoomByCode(String code)
    {
        return getRoomByCode(GameCodeGenerator.stringToInt(code));
    }

    // add player to player list
    public void addPlayer(Player player)
    {
        if (!containsPlayer(player))
        {
            players.add(player);
            playerStatuses.put(player, new PlayerStatus());
        }
    }

    // remove player from player list
    public void removePlayer(Player player)
    {
        if (containsPlayer(player))
        {
            players.remove(player);
            playerStatuses.remove(player);
        }
    }

    public boolean containsPlayer(Player player)
    {
        return players.contains(player);
    }

    public boolean containsPlayer(String name)
    {
        for (Player player : players)
            if (player.getName().equals(name))
                return true;
        return false;
    }

    public boolean isFull()
    {
        return players.size() == 4;
    }

    public boolean isEmpty()
    {
        return players.isEmpty();
    }

    public static void removeRoom(int code)
    {
        if (rooms.containsKey(code)) rooms.remove(code);
    }

    public boolean isPlayerReady(Player player)
    {
        return playerStatuses.get(player).isReady();
    }

    public void setPlayerReady(Player player, boolean isReady)
    {
        playerStatuses.get(player).setReady(isReady);
    }
}

class PlayerStatus
{
    private boolean isReady;

    public PlayerStatus()
    {
        isReady = false;
    }

    public boolean isReady()
    {
        return isReady;
    }

    public void setReady(boolean ready)
    {
        isReady = ready;
    }
}