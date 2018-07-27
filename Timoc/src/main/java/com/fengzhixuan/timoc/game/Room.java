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
        if (!containsPlayer(player)) players.add(player);
    }

    // remove player from player list
    public void removePlayer(Player player)
    {
        players.remove(player);
    }

    public boolean containsPlayer(Player player)
    {
        return players.contains(player);
    }

    public boolean isFull()
    {
        return players.size() == 4;
    }

    public boolean isEmpty()
    {
        return players.isEmpty();
    }
}
