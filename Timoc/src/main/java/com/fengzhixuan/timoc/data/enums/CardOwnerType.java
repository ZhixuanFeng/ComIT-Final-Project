package com.fengzhixuan.timoc.data.enums;

public enum CardOwnerType
{
    // 0 starter allCards belong to all players
    AllPlayers,

    // 1 allCards that are in a player's deck
    Player_In_Deck,

    // 2 allCards belong to a player but not in their deck
    Player_Not_In_Deck,

    // 3 allCards being sold on the market
    Market,

    // 4 allCards that are only owned by some enemy
    Enemy,

    // 5 no owner, this is not used for now
    NoOwner
}
