package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Player;

public interface PlayerService
{
    Player findPlayerByName(String name);

    void savePlayer(Player player);

    void addGold(Player player, int gold);

    void removeGold(Player player, int gold);
}
