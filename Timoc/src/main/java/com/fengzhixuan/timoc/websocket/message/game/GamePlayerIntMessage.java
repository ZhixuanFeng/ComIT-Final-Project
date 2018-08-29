package com.fengzhixuan.timoc.websocket.message.game;

// for messages contains a type, a player name and a value
public class GamePlayerIntMessage extends GamePlayerMessage
{
    private int value;

    public GamePlayerIntMessage(int type, int id, int value)
    {
        super(type, id);
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
