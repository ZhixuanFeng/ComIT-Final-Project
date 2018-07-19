package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.Offer;
import com.fengzhixuan.timoc.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MarketRESTController
{
    @Autowired
    private OfferService offerService;

    @RequestMapping(value = "/market/all", method = RequestMethod.POST)
    public List<Offer> getAllOffers()
    {
        return offerService.findAll();
    }

    @RequestMapping(value = "/market/filter", method = RequestMethod.POST)
    public String getOffers(@RequestParam Map<String, String> criteria)
    {
        System.out.println(criteria.get("suit"));
        return criteria.get("suit");
    }

    public static Specification<Offer> findByCriteria(final Map<String, Integer> searchCriteria) {

        return new Specification<Offer>() {

            @Override
            public Predicate toPredicate(
                    Root<Offer> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (searchCriteria.get("suit") != null) {
                    predicates.add(cb.equal(root.get("suit"), searchCriteria.get("suit")));
                }
                if (searchCriteria.get("rank") != null) {
                    predicates.add(cb.equal(root.get("rank"), searchCriteria.get("rank")));
                }
                if (searchCriteria.get("attack") != null) {
                    predicates.add(cb.equal(root.get("attack"), searchCriteria.get("attack")));
                }
                if (searchCriteria.get("block") != null) {
                    predicates.add(cb.equal(root.get("block"), searchCriteria.get("block")));
                }
                if (searchCriteria.get("heal") != null) {
                    predicates.add(cb.equal(root.get("heal"), searchCriteria.get("heal")));
                }
                if (searchCriteria.get("mana") != null) {
                    predicates.add(cb.equal(root.get("mana"), searchCriteria.get("mana")));
                }
                if (searchCriteria.get("aoe") != null) {
                    predicates.add(cb.equal(root.get("aoe"), searchCriteria.get("aoe")));
                }
                if (searchCriteria.get("taunt") != null) {
                    predicates.add(cb.equal(root.get("taunt"), searchCriteria.get("taunt")));
                }
                if (searchCriteria.get("draw") != null) {
                    predicates.add(cb.equal(root.get("draw"), searchCriteria.get("draw")));
                }
                if (searchCriteria.get("revive") != null) {
                    predicates.add(cb.equal(root.get("revive"), searchCriteria.get("revive")));
                }

                return cb.and(predicates.toArray(new Predicate[] {}));
            }
        };
    }
}
