package com.fengzhixuan.timoc.websocket.message.game;

// For messages that do not have any attributes except type
public class GameMessage
{
    protected MessageType type;

    public GameMessage() { this.type = MessageType.Empty; }

    public GameMessage(MessageType type)
    {
        this.type = type;
    }

    public MessageType getType()
    {
        return type;
    }
}
