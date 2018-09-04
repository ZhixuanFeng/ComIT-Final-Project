package com.fengzhixuan.timoc.websocket.message.room;

// class for constructing messages to send to start a game
public class RoomStartGameMessage
{
    MessageType type = MessageType.Start;

    public RoomStartGameMessage()
    {
    }

    public MessageType getType()
    {
        return type;
    }
}
