package com.fengzhixuan.timoc.websocket.message.game;

public class GameEnemyMessage extends GameMessage
{
    protected int id;

    public GameEnemyMessage(int type, int id)
    {
        super(type);
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
}
