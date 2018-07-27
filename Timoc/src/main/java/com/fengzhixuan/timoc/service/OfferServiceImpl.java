package com.fengzhixuan.timoc.service;

import com.fengzhixuan.timoc.data.entity.*;
import com.fengzhixuan.timoc.data.enums.CardOwnerType;
import com.fengzhixuan.timoc.data.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OfferServiceImpl implements OfferService
{
    @Autowired
    private UserService userService;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardDeckService cardDeckService;

    @Autowired
    private CardCollectionService cardCollectionService;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public Offer createOffer(User user, Card card, int price)
    {
        Offer offer = new Offer(user, card, price);
        // set expiry date to be 5 days later
        Date date = new Date();
        date = addDays(date, 5);
        offer.setExpDate(date);

        // move card out of a deck if it's in a deck
        CardDeck deck = card.getCardDeck();
        if (deck != null)
        {
            cardDeckService.removeCard(card, deck);
        }

        // set card owner type to market
        card.setOwnerType(CardOwnerType.Market);

        cardService.saveCard(card);
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
    public void acceptOffer(Offer offer, User buyer, User seller)
    {
        // the buyer's collection should not be full and user should have enough gold
        Card card = offer.getCard();
        int price = offer.getPrice();

        // pay gold
        userService.removeGold(buyer, price);
        userService.addGold(seller, price);

        // set card
        cardCollectionService.transferCard(card, seller, buyer); // saves card

        // remove offer
        cardService.saveCard(card);
        offerRepository.delete(offer);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public Offer findById(long id)
    {
        return offerRepository.findById(id);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public List<Offer> findByUser(User user)
    {
        return offerRepository.findByUser(user);
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
    public Page<Offer> findByCriteria(Map<String, String> criteria, Pageable pageable)
    {
        return offerRepository.findAll(criteriaToSpecification(criteria), pageable);
    }

    @Override
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public Page<Offer> findAll(Pageable pageable)
    {
        return offerRepository.findAll(pageable);
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

    public static Specification<Offer> criteriaToSpecification(final Map<String, String> searchCriteria)
    {
        return new Specification<Offer>() {

            @Override
            public Predicate toPredicate(
                    Root<Offer> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {

                Predicate finalPredicate = cb.and();
                List<Predicate> suitPredicates = new ArrayList<>();
                List<Predicate> rankPredicates = new ArrayList<>();

                if (searchCriteria.get("suit") != null)
                {
                    String[] subs = searchCriteria.get("suit").split("\\|");
                    for (String suitStr:subs)
                    {
                        suitPredicates.add(cb.or(cb.equal(root.get("suit"), suitStr)));
                    }
                    finalPredicate = cb.and(cb.or(suitPredicates.toArray(new Predicate[]{})));
                }

                if (searchCriteria.get("rank") != null)
                {
                    String[] subs = searchCriteria.get("rank").split("\\|");
                    for (String rankStr:subs)
                    {
                        rankPredicates.add(cb.or(cb.equal(root.get("rank"), rankStr)));
                    }
                    finalPredicate = cb.and(finalPredicate, cb.or(rankPredicates.toArray(new Predicate[]{})));
                }

                if (searchCriteria.get("effect") != null)
                {
                    String[] subs = searchCriteria.get("effect").split("\\&");
                    for (String effectStr:subs)
                    {
                        rankPredicates.add(cb.and(cb.greaterThan(root.get(effectStr), 0)));
                    }
                    finalPredicate = cb.and(finalPredicate, cb.and(rankPredicates.toArray(new Predicate[]{})));
                }
                return finalPredicate;
            }
        };
    }
}
