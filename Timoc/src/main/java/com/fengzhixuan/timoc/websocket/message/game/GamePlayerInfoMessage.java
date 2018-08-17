package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Player;

// for sending information of all players
public class GamePlayerInfoMessage extends GameMessage
{
    private Player[] players;

    public GamePlayerInfoMessage(Player[] players)
    {
        super(MessageType.PlayerInfo);
        this.players = players;
    }

    public Player[] getPlayers()
    {
        return players;
    }
}
