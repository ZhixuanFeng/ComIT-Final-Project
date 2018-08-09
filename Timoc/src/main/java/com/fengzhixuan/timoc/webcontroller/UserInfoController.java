package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserInfoController
{
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/userInfo/myInfo", method = RequestMethod.POST)
    public @ResponseBody
    MyInfo getMyInfo()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());

        return new MyInfo(user.getId(), user.getUsername(), user.getLevel(), user.getGold(), user.getMaxCardStorage(), user.getCardsOwned());
    }
}

// message template for the case when player asking for info of themselves
class MyInfo
{
    private long id;
    private String name;
    private int level;
    private int gold;
    private int maxCard;
    private int cardsOwned;

    public MyInfo(long id, String name, int level, int gold, int maxCard, int cardsOwned)
    {
        this.id = id;
        this.name = name;
        this.level = level;
        this.gold = gold;
        this.maxCard = maxCard;
        this.cardsOwned = cardsOwned;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getLevel()
    {
        return level;
    }

    public int getGold()
    {
        return gold;
    }

    public int getMaxCard()
    {
        return maxCard;
    }

    public int getCardsOwned()
    {
        return cardsOwned;
    }
}