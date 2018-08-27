package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.service.UserService;
import com.fengzhixuan.timoc.webcontroller.messagetemplate.MyInfoMessage;
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
    MyInfoMessage getMyInfo()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());

        return new MyInfoMessage(user.getId(), user.getUsername(), user.getLevel(), user.getGold(), user.getMaxCardStorage(), user.getCardsOwned());
    }
}

