package com.fengzhixuan.timoc.data.enums;

public enum CardOwnerType
{
    // starter cards belong to all players
    AllPlayers,

    // cards that are in a player's deck
    Player_In_Deck,

    // cards belong to a player but not in their deck
    Player_Not_In_Deck,

    // cards being sold on the market
    Market,

    // cards that are only owned by some enemy
    Enemy,

    // no owner, this is not used for now
    NoOwner
}
