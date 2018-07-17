package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.Card;
import com.fengzhixuan.timoc.data.entity.Offer;
import com.fengzhixuan.timoc.data.entity.Player;
import java.util.List;
import java.util.Map;

public interface OfferService
{
    Offer createOffer(Player player, Card card, int price);

    void cancelOffer(Offer offer);

    void acceptOffer(Offer offer, Player buyer);

//    List<Offer> getOffersBySuit(int suit);
//
//    List<Offer> getOffersByRank(int rank);
//
//    List<Offer> getOffersByIndecks(int indecks);

    List<Offer> findByPlayer(Player player);

    List<Offer> findByExpDate(String dateString);

    List<Offer> findByCriteria(Map<String, Integer> criteria);
}
