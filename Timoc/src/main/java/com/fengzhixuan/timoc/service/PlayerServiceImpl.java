package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Player;
import com.fengzhixuan.timoc.data.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService
{
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public Player findPlayerByName(String name) { return playerRepository.findByName(name); }

    @Override
    public void savePlayer(Player player)
    {
        playerRepository.save(player);
    }

    @Override
    public void addGold(Player player, int gold)
    {
        player.setGold(player.getGold() + gold);
        playerRepository.save(player);
    }

    @Override
    public void removeGold(Player player, int gold)
    {
        player.setGold(player.getGold() - gold);
        playerRepository.save(player);
    }
}
