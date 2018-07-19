"use strict";

function addCardBody(card, div) {
    var $cardDiv = $('<div>');
    var $emptyCardImg;
    if (card.id <= 52)
        $emptyCardImg = $('<img src="images/empty_card_starter.png" height="61" width="41"/>');
    else
        $emptyCardImg = $('<img src="images/empty_card.png" height="61" width="41"/>');
    var $rankSpan = $('<span></span>');
    var $suitImg = $('<img height="11" width="11"/>');
    var $cardEffectDiv = $('<div>');
    var rank = getRank(card);
    var suit = getSuit(card);
    $cardDiv.attr('id', card.id);
    $rankSpan.text(rank == 1 ? 'A' : rank == 11 ? 'J' : rank == 12 ? 'Q' : rank == 13 ? 'K' : rank);
    $rankSpan.attr('style', suit == 0 || suit == 2 ? 'color: red' : 'color:black');
    $suitImg.attr('src', suit == 0 ? 'images/diamond.png' : suit == 1 ? 'images/club.png' : suit == 2 ? 'images/heart.png' : 'images/spade.png');
    if (card.attack > 0) addCardEffect('attack', card.attack, suit, $cardEffectDiv);
    if (card.block > 0) addCardEffect('block', card.block, suit, $cardEffectDiv);
    if (card.heal > 0) addCardEffect('heal', card.heal, suit, $cardEffectDiv);
    if (card.mana > 0) addCardEffect('mana', card.mana, suit, $cardEffectDiv);
    if (card.aoe > 0) addCardEffect('aoe', card.aoe, suit, $cardEffectDiv);
    if (card.draw > 0) addCardEffect('draw', card.draw, suit, $cardEffectDiv);
    if (card.revive > 0) addCardEffect('revive', card.revive, suit, $cardEffectDiv);
    if (card.taunt > 0) addCardEffect('taunt', card.taunt, suit, $cardEffectDiv);

    $cardDiv.addClass('card').appendTo(div);
    $emptyCardImg.addClass('empty_card').appendTo($cardDiv);
    $rankSpan.addClass('rank').appendTo($cardDiv);
    $suitImg.addClass('suit').appendTo($cardDiv);
    $cardEffectDiv.addClass('card_effect').appendTo($cardDiv);
}

function addCardEffect(name, amount, suit, cardEffectDiv) {
    var $newEffectDiv = $('<div />').appendTo(cardEffectDiv);
    var $effectIconImg = $('<img class="effect_icon" height="16" width="16"/>');
    var $effectAmoundSpan = $('<span class="effect_amount""></span>');
    $effectIconImg.attr('src', 'images/' + name + '.png');
    $effectAmoundSpan.text(amount);
    $effectAmoundSpan.attr('style', suit == 0 || suit == 2 ? 'color: red' : 'color:black');
    $effectIconImg.addClass('effect_icon').appendTo($newEffectDiv);
    $effectAmoundSpan.addClass('effect_amount').appendTo($newEffectDiv);
}

function getRank(card) {
    return card.indecks - getSuit(card) * 13 + 1;
}

function getSuit(card) {
    return Math.floor(card.indecks / 13);
}