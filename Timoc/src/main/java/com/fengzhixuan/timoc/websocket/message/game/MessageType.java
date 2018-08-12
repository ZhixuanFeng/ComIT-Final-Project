package com.fengzhixuan.timoc.websocket.message.game;

public enum MessageType
{
    Empty,
    Error,
    EnterSuccessful,
    GameStart,
    RoundStart,
    AttackPhase,
    DefendPhase,
    Hand,
    NewEnemy,
    RemoveEnemy,
    EnemyDrawCard,
    EnemyPlayCard,
    PlayersTurn
}
