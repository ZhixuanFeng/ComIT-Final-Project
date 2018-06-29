package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Player;

public interface PlayerService
{
    public Player findPlayerByName(String name);
    public void savePlayer(Player player);
}
