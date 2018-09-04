package com.fengzhixuan.timoc.game;

import java.util.HashMap;
import java.util.Map;

public class Room
{
    private static Map<Integer, Room> rooms = new HashMap<>();

    private int code;
    private Player[] players = new Player[4];
    private Map<String, RoomPlayerInfo> playerStatuses = new HashMap<>();

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

    public int getCode()
    {
        return code;
    }

    public String getCodeString()
    {
        return GameCodeGenerator.intToString(code);
    }

    public Player[] getPlayers()
    {
        return players;
    }

    public RoomPlayerInfo getPlayerInfo(String name)
    {
        return playerStatuses.get(name);
    }

    public RoomPlayerInfo[] getAllPlayerInfo()
    {
        return playerStatuses.values().toArray(new RoomPlayerInfo[0]);
    }

    public static Room getRoomByCode(int code)
    {
        return rooms.getOrDefault(code, null);
    }

    public static Room getRoomByCode(String code)
    {
        return getRoomByCode(GameCodeGenerator.stringToInt(code));
    }

    // get the first empty slot in players array
    public int findFirstAvailablePosition()
    {
        int index = 0;
        while (index < players.length && players[index] != null) index++;
        return (players[index] != null) ? -1 : index;
    }

    // add player to player list
    public void addPlayer(Player player)
    {
        String name = player.getName();
        if (!playerStatuses.containsKey(name))
        {
            int position = findFirstAvailablePosition();
            players[position] = player;
            playerStatuses.put(name, new RoomPlayerInfo(player.getName(), false, position));
        }
    }

    // remove player from player list
    public void removePlayer(Player player)
    {
        String name = player.getName();
        if (playerStatuses.containsKey(name))
        {
            int position = playerStatuses.get(name).getPosition();
            players[position] = null;
            playerStatuses.remove(name);
        }
    }

    public boolean isFull()
    {
        return findFirstAvailablePosition() == -1;
    }

    public boolean isEmpty()
    {
        for (Player player : players)
            if (player != null) return false;
        return true;
    }

    public boolean containsPlayer(String name)
    {
        return playerStatuses.containsKey(name);
    }

    public static void removeRoom(int code)
    {
        rooms.remove(code);
    }

    public boolean isPlayerReady(Player player)
    {
        return playerStatuses.get(player.getName()).isReady();
    }

    public void setPlayerReady(Player player, boolean isReady)
    {
        playerStatuses.get(player.getName()).setReady(isReady);
    }

    public boolean areAllPlayersReady()
    {
        for (Map.Entry<String, RoomPlayerInfo> entry : playerStatuses.entrySet())
        {
            if (!entry.getValue().isReady()) return false;
        }
        return true;
    }
}