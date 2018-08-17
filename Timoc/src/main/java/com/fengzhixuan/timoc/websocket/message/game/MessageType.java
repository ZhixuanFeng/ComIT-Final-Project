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
    PlayerInfo,
    EnemyInfo,
    Hand,
    NewEnemy,
    RemoveEnemy,
    EnemyDrawCard,
    EnemyPlayCard,
    PlayerStartsTurn,
    PlayerEndsTurn
}
