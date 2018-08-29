package com.fengzhixuan.timoc.websocket.message.game;

// for messages contains a type, a player name and a value
public class GamePlayerIntMessage extends GamePlayerMessage
{
    private int value;

    public GamePlayerIntMessage(int type, String name, int value)
    {
        super(type, name);
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
