package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.Offer;
import com.fengzhixuan.timoc.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MarketRESTController
{
    @Autowired
    private OfferService offerService;

    @RequestMapping(value = "/market/all", method = RequestMethod.POST)
    public Page<Offer> getAllOffers(@RequestParam(value = "page") Integer page)
    {
        if (page == null) return null;
        return offerService.findAll(PageRequest.of(page, 20));
    }

    @RequestMapping(value = "/market/card_id", method = RequestMethod.POST)
    public Offer getOffersByCardId(@RequestParam(value = "id") Long id)
    {
        if (id == null) return null;
        return offerService.findByCardId(id);
    }

    @RequestMapping(value = "/market/filter", method = RequestMethod.POST)
    public Page<Offer> getOffers(@RequestParam Map<String, String> criteria, @RequestParam(value = "page") Integer page)
    {
        return offerService.findByCriteria(criteria, PageRequest.of(page, 20));
    }
}
