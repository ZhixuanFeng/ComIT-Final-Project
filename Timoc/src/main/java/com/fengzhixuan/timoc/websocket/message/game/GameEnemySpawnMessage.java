package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Enemy;

public class GameEnemySpawnMessage extends GameMessage
{
    protected Enemy enemy;

    public GameEnemySpawnMessage(int type, Enemy enemy)
    {
        super(type);
        this.enemy = enemy;
    }

    public Enemy getEnemy()
    {
        return enemy;
    }
}
