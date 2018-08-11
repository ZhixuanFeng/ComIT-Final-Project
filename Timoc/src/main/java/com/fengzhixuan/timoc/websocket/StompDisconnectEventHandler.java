package com.fengzhixuan.timoc.websocket;

import com.fengzhixuan.timoc.game.GameCodeGenerator;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.game.Room;
import com.fengzhixuan.timoc.websocket.message.room.RoomLeaveMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class StompDisconnectEventHandler implements ApplicationListener<SessionDisconnectEvent>
{
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event)
    {
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(event.getMessage());
        if (StompCommand.DISCONNECT.equals(headerAccessor.getCommand()))
        {
            // get information
            Principal userPrincipal = headerAccessor.getUser();
            if (userPrincipal == null) { return; }
            String username = userPrincipal.getName();
            Player player = Player.findPlayerByName(username);
            if (player == null) { return; }
            String codeString = player.getCode();
            Room room = Room.getRoomByCode(codeString);

            // if room is not null, it means that user disconnects when being in a room
            if (room != null)
            {
                room.removePlayer(player);
                if (room.isEmpty())
                {
                    // remove empty room
                    Room.removeRoom(GameCodeGenerator.stringToInt(codeString));
                }
                else
                {
                    // if there are still players in this room, tell them a player left
                    RoomLeaveMessage message = new RoomLeaveMessage(username);
                    messagingTemplate.convertAndSend("/topic/room/" + codeString, message);
                }
                Player.removePlayer(username);
            }
        }
    }
}
