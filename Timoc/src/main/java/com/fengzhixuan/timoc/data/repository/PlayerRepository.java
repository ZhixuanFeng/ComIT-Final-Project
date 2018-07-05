package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long>
{
   Player findById(long id);
   Player findByName(String name);
}
