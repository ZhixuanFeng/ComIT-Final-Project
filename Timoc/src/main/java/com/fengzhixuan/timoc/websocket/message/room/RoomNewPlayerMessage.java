package com.fengzhixuan.timoc.websocket.message.room;

import com.fengzhixuan.timoc.game.RoomPlayerInfo;

public class RoomNewPlayerMessage
{
    MessageType type = MessageType.Enter;
    RoomPlayerInfo player;

    public RoomNewPlayerMessage(RoomPlayerInfo player)
    {
        this.player = player;
    }

    public MessageType getType()
    {
        return type;
    }

    public RoomPlayerInfo getPlayer()
    {
        return player;
    }
}
