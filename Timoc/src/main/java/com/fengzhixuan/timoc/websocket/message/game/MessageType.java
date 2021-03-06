package com.fengzhixuan.timoc.websocket.message.game;

// message code, camel-case naming
public class MessageType
{
    public static final int Empty = 0;  // unused
    public static final int Error = 1;  // all error related messages
    public static final int EnterSuccessful = 2;  // when each player enters a room
    public static final int GameStart = 3;  // signals the start of a game
    public static final int RoundStart = 4;  // signals the start of a round
    public static final int AttackPhase = 5;  // start of attack phase
    public static final int DefendPhase = 6;  // start of defend phase
    public static final int GameInfo = 7;  // send information about the game, used when a player enters/re-enters a game
    public static final int PlayerInfo = 8;  // send all player information, used when a player enters/re-enters a game
    public static final int EnemyInfo = 9;  // send all enemy information, used when a player enters/re-enters a game
    public static final int PlayerDeck = 10;
    public static final int Hand = 11;  // message containing cards in hand
    public static final int EnemyDies = 12;  // when a enemy hp drops to 0
    public static final int RemoveEnemy = 13;  // when a enemy dies, send at the end of a round
    public static final int EnemyDrawsCard = 14;  // enemy draws card(s)
    public static final int EnemyStartsTurn = 15;
    public static final int EnemyEndsTurn = 16;
    public static final int EnemyUpdateAll = 17;  // update all enemy information
    public static final int PlayerStartsTurn = 18;  // start of a player's turn
    public static final int PlayerEndsTurn = 19;  // a player finishes their turn
    public static final int PlayerUpdate = 20;  // update player information
    public static final int PlayerUpdateAll = 21;  // update all player information
    public static final int PlayerRevive = 22;  // when a player revives
    public static final int PlayerDrawCard = 23;  // when player draw card(not at the start of the round)
    public static final int PlayerDies = 24;  // When a player hp drops to 0
    public static final int RemovePlayedCards = 25;

    public static final int PlayerHpChange = 26;
    public static final int PlayerManaChange = 27;
    public static final int PlayerBlockChange = 28;
    public static final int PlayerHateChange = 29;
    public static final int EnemyHpChange = 30;
    public static final int DState = 31;
    public static final int NotEnoughMana = 32;
    public static final int NoMoreReplace = 33;
    public static final int PlayerPlaysCard_Player = 34;
    public static final int PlayerPlaysCard_Enemy = 35;
    public static final int EnemyPlaysCard_Player = 36;
    public static final int EnemyPlaysCard_Enemy = 37;
    public static final int PlayerDisconnected = 38;
    public static final int PlayerReconnected = 39;
    public static final int DisconnectDisplay = 40;
    public static final int GameOverVictory = 41;
    public static final int GameOverDefeat = 42;
    public static final int GameEndReward = 43;
    public static final int RemoveCardAtPosition = 44;
}
