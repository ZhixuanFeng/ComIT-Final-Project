package com.fengzhixuan.timoc.websocket;

import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.GameCodeGenerator;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.game.Room;
import com.fengzhixuan.timoc.websocket.message.game.GamePlayerMessage;
import com.fengzhixuan.timoc.websocket.message.game.MessageType;
import com.fengzhixuan.timoc.websocket.message.room.RoomLeaveMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

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
            int codeInt = GameCodeGenerator.stringToInt(codeString);
            Room room = Room.getRoomByCode(codeString);

            // for now, don't worry when disconnecting from a display
            String sessionId = headerAccessor.getSessionId(); // Session ID
            String channel = StompSubscribeEventHandler.sessionIdChannelMap.get(sessionId);
            if (channel == null) return;

            // user disconnects when being in a room
            if (channel.equals("room") && room != null)
            {
                room.removePlayer(player);
                if (room.isEmpty())
                {
                    // remove empty room
                    Room.removeRoom(codeInt);
                }
                else
                {
                    // if there are still players in this room, tell them a player left
                    RoomLeaveMessage message = new RoomLeaveMessage(username);
                    messagingTemplate.convertAndSend("/topic/room/" + codeString, message);
                }
                Player.removePlayer(username);
            }

            Game game = Game.getGameByCode(codeInt);
            if (channel.equals("controller") && game != null)
            {
                game.setPlayerOnlineStatus(username, false);

                // show that a player disconnected
                game.addDisplayMessage(new GamePlayerMessage(MessageType.PlayerDisconnected, player.getId()));

                if (!game.isAnyPlayerConnected())
                {
                    // remove empty game
                    Game.removeGame(codeInt);
                }
                // flush the generated message
                game.flushMessages();
            }

            StompSubscribeEventHandler.sessionIdChannelMap.remove(sessionId);
        }
    }
}
