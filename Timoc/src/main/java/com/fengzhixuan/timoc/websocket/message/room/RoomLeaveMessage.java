package com.fengzhixuan.timoc.websocket.message.room;

// class for constructing messages to send when players leave rooms
public class RoomLeaveMessage
{
    MessageType type = MessageType.Leave;
    String name;

    public RoomLeaveMessage(String name)
    {
        this.name = name;
    }

    public MessageType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }
}
