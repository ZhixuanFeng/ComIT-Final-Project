package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.game.Room;
import com.fengzhixuan.timoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class RoomController
{
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/room/create", method = RequestMethod.POST)
    public @ResponseBody
    String createRoom()
    {
        Room room = Room.createRoom();
        return room.getCodeString();
    }

    @MessageMapping("/room.enter/{code}")
    @SendTo("/topic/room/{code}")
    public String enterRoom(@DestinationVariable String code, Principal principal)
    {
        User user = userService.findUserByUsername(principal.getName());
        return principal.getName() + " entered room " + code;
    }
}
