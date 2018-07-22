package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.Offer;
import com.fengzhixuan.timoc.data.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OfferService
{
    Offer createOffer(Player player, Card card, int price);

    void cancelOffer(Offer offer);

    void acceptOffer(Offer offer, Player buyer, Player seller);

    Offer findById(long id);

    List<Offer> findByPlayer(Player player);

    List<Offer> findByExpDate(String dateString);

    Offer findByCardId(long id);

    Page<Offer> findByCriteria(Map<String, String> criteria, Pageable pageable);

    Page<Offer> findAll(Pageable pageable);
}
