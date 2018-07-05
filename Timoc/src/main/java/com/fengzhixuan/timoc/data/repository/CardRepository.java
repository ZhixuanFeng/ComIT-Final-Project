package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>
{
    Card findById(long id);

//    @Query(value = "select c from card where card.card_deck_id=:deck_id", nativeQuery = true)
//    List<Card> findCardsByDeckId();
    List<Card> findByCardDeckId(long deck_id);
}
