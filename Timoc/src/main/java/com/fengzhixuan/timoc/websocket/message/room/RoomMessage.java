package com.fengzhixuan.timoc.websocket.message.room;

public class RoomMessage
{
    MessageType type;

    public RoomMessage(MessageType type)
    {
        this.type = type;
    }

    public MessageType getType()
    {
        return type;
    }
}
