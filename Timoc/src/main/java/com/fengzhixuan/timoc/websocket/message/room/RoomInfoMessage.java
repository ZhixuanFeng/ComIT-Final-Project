package com.fengzhixuan.timoc.websocket.message.room;

import com.fengzhixuan.timoc.game.RoomPlayerInfo;

// class for constructing messages to broadcast when players enter rooms
public class RoomInfoMessage
{
    MessageType type = MessageType.Info;  // for client side to understand the message type
    RoomPlayerInfo[] players;

    public RoomInfoMessage(RoomPlayerInfo[] players)
    {
        this.players = players;
    }

    public MessageType getType()
    {
        return type;
    }

    public RoomPlayerInfo[] getPlayers()
    {
        return players;
    }
}
