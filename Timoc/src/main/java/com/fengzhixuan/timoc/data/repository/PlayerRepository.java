package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends CrudRepository<Player, Long>
{
    //Player findByNumber(String number);

    //List<Player> findByName(String name);
}
