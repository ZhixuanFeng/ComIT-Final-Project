package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.CardCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardCollectionRepository extends JpaRepository<CardCollection, Long>
{
    CardCollection findById(long id);
}
