package com.fengzhixuan.timoc.websocket.message.game;

// for messages about a player that only includes message type and the player's name
public class GamePlayerMessage extends GameMessage
{
    protected int id;

    public GamePlayerMessage(int type, int id)
    {
        super(type);
        this.id = id;
    }

    public int getName()
    {
        return id;
    }
}
