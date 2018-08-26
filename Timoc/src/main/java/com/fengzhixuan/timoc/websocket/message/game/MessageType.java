package com.fengzhixuan.timoc.websocket.message.game;

public enum MessageType
{
    Empty,  // unused
    Error,  // all error related messages
    EnterSuccessful,  // when each player enters a room
    GameStart,  // signals the start of a game
    RoundStart,  // signals the start of a round
    AttackPhase,  // start of attack phase
    DefendPhase,  // start of defend phase
    GameInfo,  // send information about the game, used when a player enters/re-enters a game
    PlayerInfo,  // send all player information, used when a player enters/re-enters a game
    EnemyInfo,  // send all enemy information, used when a player enters/re-enters a game
    Hand,  // message containing cards in hand
    NewEnemy,  // when a enemy spawns
    RemoveEnemy,  // when a enemy dies, send at the end of a round
    EnemyDrawCard,  // enemy draws card(s)
    EnemyPlayCard,  // enemy plays card(s)
    EnemyUpdate,  // update enemy information
    EnemyUpdateAll,  // update all enemy information
    PlayerStartsTurn,  // start of a player's turn
    PlayerEndsTurn,  // a player finishes their turn
    PlayerUpdate,  // update player information
    PlayerUpdateAll,  // update all player information
    PlayerRevive,  // when a player revives
    PlayerDrawCard,  // when player draw card(not at the start of the round)
    PlayCardSuccessful,  // send when a play card command is accepted
    PlayCardFailed,  // send when a play card command failed
    DiscardCardSuccessful,  // send when a discard card command is accepted
    DiscardCardFailed,  // send when a discard card command failed

    PlayerHpChange,
    PlayerManaChange,
    PlayerBlockChange,
    PlayerHateChange,
    EnemyHpChange
}
