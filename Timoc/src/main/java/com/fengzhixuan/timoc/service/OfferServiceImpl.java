package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.*;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.data.repository.OfferRepository;
import com.fengzhixuan.timoc.webcontroller.MarketRESTController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OfferServiceImpl implements OfferService
{
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CardDeckService cardDeckService;

    @Autowired
    private CardCollectionService cardCollectionService;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public Offer createOffer(Player player, Card card, int price)
    {
        Offer offer = new Offer(player, card, price);
        // set expiry date to be 5 days later
        Date date = new Date();
        date = addDays(date, 5);
        offer.setExpDate(date);

        // set card owner type to market
        card.setOwnerType(CardOwnerType.Market);
        // move card out of a deck if it's in a deck
        CardDeck deck = card.getCardDeck();
        if (deck != null)
        {
            cardDeckService.removeCard(card, deck);
        }

        offerRepository.save(offer);
        return offer;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void cancelOffer(Offer offer)
    {
        Card card = offer.getCard();
        card.setOwnerType(CardOwnerType.Player_Not_In_Deck);

        cardService.saveCard(card);
        offerRepository.delete(offer);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void acceptOffer(Offer offer, Player buyer)
    {
        // assuming the buyer's collection is not full and player has enough gold
        Card card = offer.getCard();
        Player seller = offer.getPlayer();

        // pay gold
        playerService.removeGold(buyer, offer.getPrice());
        playerService.addGold(seller, offer.getPrice());

        // get card
        card.setOwnerType(CardOwnerType.Player_Not_In_Deck);
        cardCollectionService.transferCard(card, seller.getCardCollection(), buyer.getCardCollection());

        // remove offer
        offerRepository.delete(offer);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public Offer findById(long id)
    {
        return offerRepository.getOne(id);
    }

//    @Override
//    public List<Offer> getOffersBySuit(int suit)
//    {
//        return offerRepository.findBySuit(suit);
//    }
//
//    @Override
//    public List<Offer> getOffersByRank(int rank)
//    {
//        return offerRepository.findByRank(rank);
//    }
//
//    @Override
//    public List<Offer> getOffersByIndecks(int indecks)
//    {
//        return offerRepository.findByIndecks(indecks);
//    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public List<Offer> findByPlayer(Player player)
    {
        return offerRepository.findByPlayer(player);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public List<Offer> findByExpDate(String dateString)
    {
        Date date = createDateFromDateString(dateString);

        return offerRepository.findByExpDate(date);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public Offer findByCardId(long id)
    {
        return offerRepository.findByCardId(id);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public List<Offer> findByCriteria(Map<String, Integer> criteria)
    {
        return offerRepository.findAll(MarketRESTController.findByCriteria(criteria));
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public List<Offer> findAll()
    {
        return offerRepository.findAll();
    }

    // helper method for creating a Date object from a string
    private Date createDateFromDateString(String dateString)
    {
        Date date = null;
        if(null!=dateString)
        {
            try
            {
                date = DATE_FORMAT.parse(dateString);
            }catch(ParseException pe)
            {
                date = new Date();
            }
        }
        else
        {
            date = new Date();
        }
        return date;
    }

    // helper method for adding days to a date
    public static Date addDays(Date date, int days)
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }
}
