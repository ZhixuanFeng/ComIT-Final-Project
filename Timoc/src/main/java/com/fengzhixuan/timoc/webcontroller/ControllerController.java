package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.data.entity.CardCollection;
import com.fengzhixuan.timoc.data.entity.User;
import com.fengzhixuan.timoc.game.Card;
import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.service.CardCollectionService;
import com.fengzhixuan.timoc.service.CardService;
import com.fengzhixuan.timoc.service.UserService;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class ControllerController
{
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardCollectionService cardCollectionService;

    @MessageMapping("/controller.enter/{code}")
    @SendTo("/topic/controller/{code}")
    public GameMessage startDisplay(@DestinationVariable String code, Principal principal)
    {
        if (principal == null) { return new GameErrorMessage("User not found"); }
        String username = principal.getName();

        Game game = Game.getGameByCode(code);
        if (game == null) { return new GameErrorMessage("Game not found"); }

        Player player = Player.findPlayerByName(username);
        if (player == null) { return new GameErrorMessage("Player not found"); }

        if (!game.isPlayerInThisGame(username)) { return new GameErrorMessage("Error joining this game"); }

        // set status
        game.setPlayerOnlineStatus(username, true);

        // if game is already started, meaning this player is reconnecting
        if (game.isGameStarted())
        {
            messagingTemplate.convertAndSend("/topic/display/" + code, new GamePlayerMessage(MessageType.PlayerReconnected, player.getId()));
            return new GameMessage(MessageType.GameStart);
        }
        // if game is not started yet
        else
        {
            // if all players are connected and a display is setup, start game
            if (game.isDisplayConnected() && game.areAllPlayersConnected())
            {
                game.gameStart(messagingTemplate);
            }
        }

        return new GameMessage(MessageType.EnterSuccessful);
    }

    @MessageMapping("/controller/{code}")
    @SendTo("/topic/display/{code}")
    public DisplayStateMessage buttonPress(@DestinationVariable String code, Principal principal, int buttonCode)
    {
        Game game = Game.getGameByCode(code);
        if (game == null) return null;
        if (!game.getCurrentPlayer().getName().equals(principal.getName())) return null;

        Integer states = game.processControllerInput(buttonCode);
        return states == null ? null : new DisplayStateMessage(states);
    }

    @MessageMapping("/controller.rewards/{code}")
    public void collectRewards(@DestinationVariable String code, Principal principal)
    {
        if (principal == null) return;
        String username = principal.getName();

        Game game = Game.getGameByCode(code);
        if (game == null) return;

        User user = userService.findUserByUsername(username);
        CardCollection cardCollection = cardCollectionService.findById(user.getId());

        Player player = Player.findPlayerByName(username);
        if (player == null) return;

        // give card rewards
        List<Integer> qualities = player.getCardRewardQualities();
        int quantity = qualities.size();
        // don't generate more cards than user's available storage room
        int userStorageRemainingSpace = user.getMaxCardStorage() - user.getCardsOwned();
        if (userStorageRemainingSpace < quantity) quantity = userStorageRemainingSpace;
        Card[] cards = new Card[quantity];
        if (quantity > 0)
        {
            for (int i = 0; i < quantity; i++)
            {
                com.fengzhixuan.timoc.data.entity.Card cardEntity = cardService.createCard(qualities.get(i), 10);
                cardCollectionService.addCard(cardEntity, cardCollection, user);
                cards[i] = new Card(cardEntity.getIndecks(), cardEntity.getSuit(),
                        cardEntity.getRank(), cardEntity.getAttack(),
                        cardEntity.getBlock(), cardEntity.getHeal(),
                        cardEntity.getMana(), cardEntity.getAoe(),
                        cardEntity.getDraw(), cardEntity.getRevive(),
                        cardEntity.getTaunt());
            }
            player.getCardRewardQualities().clear();
        }

        // give gold rewards
        int gold = player.getGoldRewards();
        if (gold > 0)
        {
            userService.addGold(user, gold);
            player.clearGoldReward();
        }

        messagingTemplate.convertAndSendToUser(username, "/topic/controller/" + code, new GameRewardMessage(cards, gold));
    }
}
