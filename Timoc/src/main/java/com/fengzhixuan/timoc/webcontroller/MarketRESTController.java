package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.Offer;
import com.fengzhixuan.timoc.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/market/card_id", method = RequestMethod.POST)
    public Offer getAllOffers(@RequestParam(value = "id") Long id)
    {
        if (id == null) return null;
        return offerService.findByCardId(id);
    }

    @RequestMapping(value = "/market/filter", method = RequestMethod.POST)
    public List<Offer> getOffers(@RequestParam Map<String, String> criteria)
    {
        return offerService.findByCriteria(criteria);
    }
}
