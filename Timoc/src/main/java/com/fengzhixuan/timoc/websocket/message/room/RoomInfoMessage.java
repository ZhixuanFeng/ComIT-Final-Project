package com.fengzhixuan.timoc.websocket.message.room;

import com.fengzhixuan.timoc.game.Player;

import java.util.ArrayList;
import java.util.List;

// class for constructing messages to broadcast when players enter rooms
public class RoomInfoMessage
{
    MessageType type = MessageType.Info;  // for client side to differentiate the message type
    List<PlayerInfo> players;

    RoomInfoMessage(List<PlayerInfo> players)
    {
        this.players = players;
    }

    public static RoomInfoMessage createMessage(List<Player> players)
    {
        List<PlayerInfo> names = new ArrayList<>();
        for (Player player : players)
        {
            names.add(new PlayerInfo(player.getName()));
        }
        return new RoomInfoMessage(names);
    }

    public MessageType getType()
    {
        return type;
    }

    public List<PlayerInfo> getPlayers()
    {
        return players;
    }
}
