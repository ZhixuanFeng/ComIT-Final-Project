package com.fengzhixuan.timoc.websocket;

import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.GameCodeGenerator;
import com.fengzhixuan.timoc.game.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Component
public class StompSubscribeEventHandler implements ApplicationListener<SessionSubscribeEvent>
{
    // stores what channel the session is connected to
    public static Map<String, String> sessionIdChannelMap = new HashMap<>();

    @Autowired
    @Qualifier("clientOutboundChannel")
    private MessageChannel clientOutboundChannel;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event)
    {
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(event.getMessage());
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand()))
        {
            Principal userPrincipal = headerAccessor.getUser();
            String destination = headerAccessor.getDestination();
            String result = validateSubscription(userPrincipal, destination);
            if (result != null)
            {
                StompHeaderAccessor errorHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
                errorHeaderAccessor.setMessage(result);
                errorHeaderAccessor.setSessionId(headerAccessor.getSessionId());
                this.clientOutboundChannel.send(MessageBuilder.createMessage(new byte[0], errorHeaderAccessor.getMessageHeaders()));
            }
            else
            {
                String channel = destination.split("\\/")[2];
                if (!channel.equals("topic")) sessionIdChannelMap.put(headerAccessor.getSessionId(), channel);
            }
        }
    }

    // return null if valid
    private String validateSubscription(Principal principal, String topicDestination)
    {
        // invalid path
        if (topicDestination == null)
        {
            return "Invalid request";
        }

        // split with "/", subs[0] should be "", subs[1] should be "topic"
        String[] subs = topicDestination.split("\\/");

        if (principal == null && !subs[2].equals("display"))
        {
            // unauthenticated user
            return "Unauthorized access";
        }

        // invalid path
        if (subs.length < 4)
        {
            return "Invalid request";
        }

        int index = 1;
        if (subs[index].equals("user"))
        {
            index++;
        }
        if (subs[index].equals("topic"))
        {
            index++;
        }
        else
        {
            return "Invalid request";
        }

        // room subscription
        if (subs[index].equals("room"))
        {
            index++;
            String code = subs[index];
            // code invalid
            if (!GameCodeGenerator.isCodeValid(code))
            {
                return "Invalid code";
            }

            Room room = Room.getRoomByCode(code);
            // room not exist
            if (room == null)
            {
                return "Invalid code";
            }

            // room full
            if (!room.containsPlayer(principal.getName()) && room.isFull())
            {
                return "Room full";
            }

//            // the post request right before this subscription puts player object into the room object
//            // unless some error happens or user modifies the front end, otherwise this shouldn't happen
//            if (!room.containsPlayer(principal.getName()))
//            {
//                return "Error";
//            }
        }

        // controller subscription
        else if (subs[index].equals("controller"))
        {
            index++;
            String code = subs[index];
            // code invalid
            if (!GameCodeGenerator.isCodeValid(code))
            {
                return "Invalid code";
            }

            Game game = Game.getGameByCode(code);
            // game not exist
            if (game == null)
            {
                return "Invalid code";
            }

            // check if the user is subscribing to the correct code
            if (!game.isPlayerInThisGame(principal.getName()))
            {
                return "Error";
            }
        }
        else if (subs[index].equals("display"))
        {
            // maybe private game
        }
        else
        {
            return "Invalid channel";
        }
        return null;
    }
}
