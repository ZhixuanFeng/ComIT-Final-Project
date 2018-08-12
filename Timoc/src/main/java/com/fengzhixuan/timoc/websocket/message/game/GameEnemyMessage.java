package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Enemy;

public class GameEnemyMessage extends GameMessage
{
    protected Enemy enemy;

    public GameEnemyMessage(MessageType type, Enemy enemy)
    {
        this.type = type;
        this.enemy = enemy;
    }

    public Enemy getEnemy()
    {
        return enemy;
    }
}
