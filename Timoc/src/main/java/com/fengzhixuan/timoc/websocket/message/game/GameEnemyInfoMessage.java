package com.fengzhixuan.timoc.websocket.message.game;

import com.fengzhixuan.timoc.game.Enemy;

// for sending information of all enemies
public class GameEnemyInfoMessage extends GameMessage
{
    private Enemy[] enemies;

    public GameEnemyInfoMessage(int type, Enemy[] enemies)
    {
        super(type);
        this.enemies = enemies;
    }

    public Enemy[] getEnemies()
    {
        return enemies;
    }
}
