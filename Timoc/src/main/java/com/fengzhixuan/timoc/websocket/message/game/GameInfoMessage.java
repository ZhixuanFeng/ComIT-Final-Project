package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Game;

// for sending information about the game
public class GameInfoMessage extends GameMessage
{
    private Game game;

    public GameInfoMessage(Game game)
    {
        super(MessageType.GameInfo);
        this.game = game;
    }

    public Game getGame()
    {
        return game;
    }
}
