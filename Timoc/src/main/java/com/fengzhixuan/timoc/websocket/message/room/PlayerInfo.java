package com.fengzhixuan.timoc.websocket.message.room;

public class PlayerInfo
{
    String name;
    boolean ready;

    public PlayerInfo(String name, boolean ready)
    {
        this.name = name;
        this.ready = ready;
    }

    public String getName()
    {
        return name;
    }

    public boolean isReady()
    {
        return ready;
    }
}
