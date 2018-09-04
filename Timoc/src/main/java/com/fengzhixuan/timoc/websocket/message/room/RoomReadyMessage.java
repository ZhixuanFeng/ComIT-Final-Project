package com.fengzhixuan.timoc.websocket.message.room;

// class for constructing messages to send when players clicks ready or unready
public class RoomReadyMessage
{
    MessageType type;
    String name;
    boolean isReady;

    public RoomReadyMessage(MessageType type, String name, boolean isReady)
    {
        this.type = type;
        this.name = name;
        this.isReady = isReady;
    }

    public MessageType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public boolean isReady()
    {
        return isReady;
    }
}
