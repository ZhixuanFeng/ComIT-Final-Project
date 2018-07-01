package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.CardDeck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("cardDeckRepository")
public interface CardDeckRepository extends JpaRepository<CardDeck, Long>
{
}
