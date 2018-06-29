package com.fengzhixuan.timoc.data.service;

import com.fengzhixuan.timoc.data.entity.Player;
import com.fengzhixuan.timoc.data.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PlayerController
{
    @Autowired
    private PlayerRepository repository;

    @RequestMapping(value="/players", method=RequestMethod.GET)
    List<Player> findAll(@RequestParam(required = false) String playerNumber)
    {
        List<Player> players = new ArrayList<>();
        if (playerNumber == null)
        {
            Iterable<Player> results = this.repository.findAll();
            results.forEach(player -> {players.add(player);});
        }

        return players;
    }
}
