package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("cardRepository")
public interface CardRepository extends JpaRepository<Card, Long>
{
    Card findById(long id);
}
