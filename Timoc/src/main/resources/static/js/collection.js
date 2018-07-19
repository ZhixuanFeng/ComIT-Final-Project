"use strict";

var collection = [];
var deck = [];
var collectionAndStarter = [];

$(document).ready(function()
{
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    var $cardArea = $('#card_area');
    var $overlay = $('#overlay');
    var $message = $('#message');
    $.getJSON("/rest/cards", function (json) {
        // find cards the player owns
        json.forEach(function (card) {
            var index = 0;
            while (index < collection.length &&
                (card.indecks > collection[index].indecks ||
                (card.indecks == collection[index].indecks && card.quality < collection[index].quality)))
                index++;
            collection.splice(index, 0, card);

            if (card.ownerType == 1)
                deck[card.indecks] = card;
        });

        collectionAndStarter = combineSortedCardPiles(collection, starter);

        displayCards(collectionAndStarter, $cardArea);

        $cardArea.children('.card').click(function () {
            var card = undefined;
            for (var i = 0; i < collection.length && typeof(card) == 'undefined'; i++)
                if (collection[i].id == this.id)
                    card = collection[i];
            if (typeof(card) != 'undefined') {
                $overlay.show();
                $('#ol_buttons').show();
                $('#ol_confirm_buttons').hide();
                $('#ol_selling').hide();

                $('#ol_card').empty();
                $message.empty();
                displayCards([card], $('#ol_card'));
                $('#bt_turn_into_gold').val('turn into gold: ' + (50 + card.quality));
            }
        });

        $('#bt_dismiss, #bt_cancel').click(function () {
            $('#overlay').hide();
        });

        $('#bt_add_to_deck').click(function () {
            var id = $('#ol_card').children('.card')[0].id;
            if (id > 52) {
                var i;
                for (i = 0; i < collection.length && collection[i].id != id; i++);
                var card = collection[i];

                if (deck[card.indecks].id == id) {
                    $message.text("This card is already in your deck");
                    return;
                }

                $.post('/deck/set_card', {id:id}, function () {
                    card.ownerType = 1;
                    if (typeof(deck[card.indecks]) != 'undefined') {
                        deck[card.indecks].ownerType = 2;
                    }
                    deck[card.indecks] = card;
                    $message.text("This card is now in your deck");
                });
            }
            else { // starter card
                if (typeof(deck[id-1]) == 'undefined') {
                    $message.text("This card is already in your deck");
                    return;
                }

                $.post('/deck/set_card', {id:id}, function () {
                    deck[id-1].ownerType = 2;
                    deck[id-1] = undefined;
                    $message.text("This card is now in your deck");
                });
            }
        });

        $('#bt_turn_into_gold').click(function () {
            var str = $('#bt_turn_into_gold').val();
            var amount = parseInt(str.replace( /^\D+/g, ''));
            $('#ol_buttons').hide();
            $('#ol_confirm_buttons').show();
            $message.text('Turn this card into ' + amount + ' gold?');
            // TODO: warn player if this card is in deck
            $('#bt_confirm').click(function () {
                // TODO: send post request
                $('#overlay').hide();
            });
        });

        $('#bt_sell').click(function () {
            $('#ol_buttons').hide();
            $('#ol_selling').show();
            $message.text('Put this card for sale on the market');

            $('#bt_confirm_sell').click(function () {
                var input = parseInt($('#price').val());
                if (input > 9999 || input <= 0) {
                    $message.text('Price can only be within 1~9999');
                    return;
                }

                $('#ol_selling').hide();
                $('#ol_confirm_buttons').show();
                $message.text('Sell on the market for ' + input + ' gold?');
                // TODO: warn player if this card is in deck
                $('#bt_confirm').click(function () {
                    //$.post();
                    $('#overlay').hide();
                });
            });
        });
    });
});

$(document).mouseup(function(e) {
    var container = $('#overlay');
    if (!container.is(e.target) && container.has(e.target).length === 0)
    {
        container.hide();
    }
});

function combineSortedCardPiles(cards1, cards2) {
    var cards = [];
    for (var i = 0; i < cards1.length; i++)
        cards[i] = cards1[i];

    var index = 0;
    for (var i = 0; i < cards2.length; i++) {
        while (index < cards.length &&
            (cards2[i].indecks > cards[index].indecks ||
            (cards2[i].indecks == cards[index].indecks &&
                cards2[i].quality <= cards[index].quality)))
            index++;
        cards.splice(index, 0, cards2[i]);
        index++;
    }
    return cards;
}

function displayCards(cards, $div) {
    for (var i = 0; i < cards.length; i++) {
        var card = (typeof(cards[i]) == 'undefined') ? starter[currentSuit * 13 + i] : cards[i];
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
        if (card.attack > 0) addCardEffect('attack', card.attack, suit);
        if (card.block > 0) addCardEffect('block', card.block, suit);
        if (card.heal > 0) addCardEffect('heal', card.heal, suit);
        if (card.mana > 0) addCardEffect('mana', card.mana, suit);
        if (card.aoe > 0) addCardEffect('aoe', card.aoe, suit);
        if (card.draw > 0) addCardEffect('draw', card.draw, suit);
        if (card.revive > 0) addCardEffect('revive', card.revive, suit);
        if (card.taunt > 0) addCardEffect('taunt', card.taunt, suit);

        $cardDiv.addClass('card').appendTo($div);
        $emptyCardImg.addClass('empty_card').appendTo($cardDiv);
        $rankSpan.addClass('rank').appendTo($cardDiv);
        $suitImg.addClass('suit').appendTo($cardDiv);
        $cardEffectDiv.addClass('card_effect').appendTo($cardDiv);
    }

    function addCardEffect(name, amount, suit) {
        var $newEffectDiv = $('<div />').appendTo($cardEffectDiv);
        var $effectIconImg = $('<img class="effect_icon" height="16" width="16"/>');
        var $effectAmoundSpan = $('<span class="effect_amount""></span>');
        $effectIconImg.attr('src', 'images/' + name + '.png');
        $effectAmoundSpan.text(amount);
        $effectAmoundSpan.attr('style', suit == 0 || suit == 2 ? 'color: red' : 'color:black');
        $effectIconImg.addClass('effect_icon').appendTo($newEffectDiv);
        $effectAmoundSpan.addClass('effect_amount').appendTo($newEffectDiv);
    }
}

function getRank(card) {
    return card.indecks - getSuit(card) * 13 + 1;
}

function getSuit(card) {
    return Math.floor(card.indecks / 13);
}