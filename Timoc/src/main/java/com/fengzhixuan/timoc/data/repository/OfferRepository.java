package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.Offer;
import com.fengzhixuan.timoc.data.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("offerRepository")
public interface OfferRepository extends JpaRepository<Offer, Long>
{
    List<Offer> findByPlayer(Player player);

    List<Offer> findByExpDate(Date date);
}
