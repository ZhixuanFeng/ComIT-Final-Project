package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>
{
    Card findById(long id);

    List<Card> findByCardDeckId(long deck_id);

    List<Card> findByCardCollectionId(long collection_id);
}
