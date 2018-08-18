package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Player;

// message to update player status
public class GameUpdatePlayerMessage extends GameMessage
{
    private Player player;

    public GameUpdatePlayerMessage(Player player)
    {
        super(MessageType.PlayerUpdate);
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }
}
