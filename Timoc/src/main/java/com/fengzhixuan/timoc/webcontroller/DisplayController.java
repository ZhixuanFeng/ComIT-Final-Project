package com.fengzhixuan.timoc.webcontroller;

import com.fengzhixuan.timoc.game.Enemy;
import com.fengzhixuan.timoc.game.Game;
import com.fengzhixuan.timoc.game.Player;
import com.fengzhixuan.timoc.websocket.message.game.*;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DisplayController
{
    @MessageMapping("/display.enter/{code}")
    @SendTo("/topic/display/{code}")
    public GameMessage[] startDisplay(@DestinationVariable String code)
    {
        Game game = Game.getGameByCode(code);
        if (game == null) { return null; }

        GameMessage[] messages = new GameMessage[3];
        // game information
        messages[0] = new GameInfoMessage(game);
        // player information
        messages[1] = new GamePlayerInfoMessage(MessageType.PlayerInfo, game.getPlayers().values().toArray(new Player[0]));
        // enemy information
        messages[2] = new GameEnemyInfoMessage(MessageType.EnemyInfo, game.getEnemies().values().toArray(new Enemy[0]));
        return messages;
    }
}
