package com.fengzhixuan.timoc.websocket.message.game;

// for messages contains a type, an enemy id and a value
public class GameEnemyIntMessage extends GameEnemyMessage
{
    private int value;

    public GameEnemyIntMessage(int type, int id, int value)
    {
        super(type, id);
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
