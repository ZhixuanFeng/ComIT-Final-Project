package com.fengzhixuan.timoc.websocket.message.room;

// class for constructing messages to send to start a game
public class RoomStartMessage
{
    MessageType type = MessageType.Start;

    public RoomStartMessage()
    {
    }

    public MessageType getType()
    {
        return type;
    }
}
