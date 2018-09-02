package com.fengzhixuan.timoc.websocket;

import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.GameCodeGenerator;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.game.Room;
import com.fengzhixuan.timoc.service.CardCollectionService;
import com.fengzhixuan.timoc.service.CardService;
import com.fengzhixuan.timoc.service.UserService;
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
import java.util.List;
import java.util.Map;

@Component
public class StompDisconnectEventHandler implements ApplicationListener<SessionDisconnectEvent>
{
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardCollectionService cardCollectionService;

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
                    // if there is unclaimed rewards, give rewards and save it to database
                    if (game.isGameOver())
                    {
                        for (Map.Entry<String, Player> playerEntry : game.getPlayers().entrySet())
                        {
                            Player _player = playerEntry.getValue();

                            if (_player.getGoldRewards() == 0) break;  // if there's no gold, then there must be no cards
                            User user = userService.findUserByUsername(username);
                            userService.addGold(user, _player.getGoldRewards());
                            _player.clearGoldReward();

                            List<Integer> qualities = _player.getCardRewardQualities();
                            if (qualities.size() == 0) break;
                            CardCollection cardCollection = cardCollectionService.findById(user.getId());
                            int quantity = qualities.size();
                            // don't generate more cards than user's available storage room
                            int userStorageRemainingSpace = user.getMaxCardStorage() - user.getCardsOwned();
                            if (userStorageRemainingSpace < quantity) quantity = userStorageRemainingSpace;
                            if (quantity > 0)
                            {
                                while (qualities.get(0) != null)
                                {
                                    cardCollectionService.addCard(cardService.createCard(qualities.get(0), 10), cardCollection, user);
                                    qualities.remove(0);
                                }
                                _player.getCardRewardQualities().clear();
                            }
                        }
                    }

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
