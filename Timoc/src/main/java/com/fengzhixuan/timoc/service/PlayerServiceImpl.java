package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Player;
import com.fengzhixuan.timoc.data.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService
{
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public Player findPlayerByName(String name) { return playerRepository.findByName(name); }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void savePlayer(Player player)
    {
        playerRepository.save(player);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addGold(Player player, int gold)
    {
        player.setGold(player.getGold() + gold);
        playerRepository.save(player);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void removeGold(Player player, int gold)
    {
        player.setGold(player.getGold() - gold);
        playerRepository.save(player);
    }
}
