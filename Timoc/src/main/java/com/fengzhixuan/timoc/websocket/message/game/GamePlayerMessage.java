package com.fengzhixuan.timoc.websocket.message.game;

// for messages about a player that only includes message type and the player's name
public class GamePlayerMessage extends GameMessage
{
    protected String name;

    public GamePlayerMessage(int type, String name)
    {
        super(type);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
