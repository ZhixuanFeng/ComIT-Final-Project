package com.fengzhixuan.timoc.websocket.message.game;

// for messages that carries a single integer
public class GameIntMessage extends GameMessage
{
    int value;

    public GameIntMessage(MessageType type, int value)
    {
        super(type);
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
