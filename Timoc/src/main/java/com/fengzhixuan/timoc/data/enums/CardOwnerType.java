package com.fengzhixuan.timoc.data.enums;

public enum CardOwnerType
{
    // starter allCards belong to all players
    AllPlayers,

    // allCards that are in a player's deck
    Player_In_Deck,

    // allCards belong to a player but not in their deck
    Player_Not_In_Deck,

    // allCards being sold on the market
    Market,

    // allCards that are only owned by some enemy
    Enemy,

    // no owner, this is not used for now
    NoOwner
}
