package com.fengzhixuan.timoc.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fengzhixuan.timoc.game.enums.PlayerClass;

// Stores player info in room
public class RoomPlayerInfo
{
    private String name;
    private boolean ready;
    private int position;
    private int classId;

    public RoomPlayerInfo(String name, boolean ready, int position)
    {
        this.name = name;
        this.ready = ready;
        this.position = position;
        classId = position;
    }

    public String getName()
    {
        return name;
    }

    public boolean isReady()
    {
        return ready;
    }

    public void setReady(boolean ready)
    {
        this.ready = ready;
    }

    public int changeToNextClass()
    {
        classId++;
        if (classId > 4) classId = 0;
        return classId;
    }

    public int getPosition()
    {
        return position;
    }

    public int getClassId()
    {
        return classId;
    }

    @JsonIgnore
    public PlayerClass getPlayerClass()
    {
        return PlayerClass.values()[classId];
    }
}
