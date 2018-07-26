package com.fengzhixuan.timoc.websocket;

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

@Component
public class SubscribeStompEventHandler implements ApplicationListener<SessionSubscribeEvent>
{
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
            String result = validateSubscription(userPrincipal, headerAccessor.getDestination());
            if (result != null)
            {
                StompHeaderAccessor errorHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
                errorHeaderAccessor.setMessage(result);
                errorHeaderAccessor.setSessionId(headerAccessor.getSessionId());
                this.clientOutboundChannel.send(MessageBuilder.createMessage(new byte[0], errorHeaderAccessor.getMessageHeaders()));
            }
        }
    }

    // return null if valid
    private String validateSubscription(Principal principal, String topicDestination)
    {
        if (principal == null)
        {
            // unauthenticated user
            return "Unauthorized access";
        }

        // split with "/", subs[0] should be "", subs[1] should be "topic"
        String[] subs = topicDestination.split("\\/");

        // invalid path
        if (subs.length < 4)
        {
            return "Invalid request";
        }

        // invalid prefix
        if (!subs[1].equals("topic"))
        {
            return "Invalid request";
        }

        // room subscription
        if (subs[2].equals("room"))
        {
            String code = subs[3];
            // code invalid
            if (!GameCodeGenerator.isCodeValid(code))
            {
                return "Invalid code";
            }

            // room not exist
            if (Room.getRoomByCode(code) == null)
            {
                return "Invalid code";
            }
        }
        return null;
    }
}
