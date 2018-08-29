package com.fengzhixuan.timoc.websocket.message.game;

// for messages contains a type, an enemy id and a value
public class GameEnemyIntMessage extends GameMessage
{
    private int id;
    private int value;

    public GameEnemyIntMessage(int type, int id, int value)
    {
        super(type);
        this.id = id;
        this.value = value;
    }

    public int getId()
    {
        return id;
    }

    public int getValue()
    {
        return value;
    }
}
