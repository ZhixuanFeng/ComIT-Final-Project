package com.fengzhixuan.timoc.websocket.message.game;

// For messages that do not have any attributes except type
public class GameMessage
{
    protected int type;

    public GameMessage(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }
}
