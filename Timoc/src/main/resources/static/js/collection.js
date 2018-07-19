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

        // show overlay on click
        $cardArea.children('.card').click(function () {
            if ($overlay.is(':visible')) {
                $overlay.hide();
                return;
            }

            var id = this.id;
            var card = findCardById(id);
            $overlay.show();
            $('#ol_buttons').show();
            $('#ol_confirm_buttons').hide();
            $('#ol_selling').hide();

            $('#ol_card').empty();
            $message.empty();
            displayCards([card], $('#ol_card'));

            if (id <= 52) {
                // $message.text('This is a starter card');
                $('#bt_add_to_deck').show();
                $('#bt_turn_into_gold').hide();
                $('#bt_sell').hide();
                $('#bt_cancel_offer').hide();
            }
            else {
                if (card.ownerType == 3) {
                    $('#bt_add_to_deck').hide();
                    $('#bt_turn_into_gold').hide();
                    $('#bt_sell').hide();
                    $.post('market/card_id', {id:parseInt(id)}, function (offer) {
                        var offerId = offer.id;
                        var price = offer.price;
                        if (typeof(price) == 'undefined') {
                            $message.text('Cannot find this card in the market, please refresh the web page');
                        }
                        else {
                            $message.text('This card is being sold on the market for ' + price + ' gold');
                            $('#bt_cancel_offer').show();

                            $('#bt_cancel_offer').click(function () {
                                $.post('market/cancel/offer_id', {id:offerId}, function () {
                                    card.ownerType = 2;
                                    updateCardStatusDiv(card, $('#' + id));
                                    $('#overlay').hide();
                                });
                            });
                        }
                    });
                }
                else {
                    $('#bt_add_to_deck').show();
                    $('#bt_turn_into_gold').show();
                    $('#bt_sell').show();
                    $('#bt_cancel_offer').hide();
                    $('#bt_turn_into_gold').val('turn into gold: ' + (50 + card.quality));
                }
            }
        });

        $('#bt_dismiss, #bt_cancel').click(function () {
            $('#overlay').hide();
        });

        $('#bt_add_to_deck').click(function () {
            var id = $('#ol_card').children('.card')[0].id;
            if (id > 52) {
                var card = findCardById(id);
                if (card.ownerType == 3) {
                    $message.text('You may not use cards that are in the market');
                }

                if (deck[card.indecks].id == id) {
                    $message.text('This card is already in your deck');
                    return;
                }

                $.post('/deck/set_card', {id:id}, function () {
                    card.ownerType = 1;
                    if (typeof(deck[card.indecks]) != 'undefined') {
                        deck[card.indecks].ownerType = 2;
                    }
                    deck[card.indecks] = card;
                    $message.text('This card is now in your deck');
                });
            }
            else { // starter card
                if (typeof(deck[id-1]) == 'undefined') {
                    $message.text('This card is already in your deck');
                    return;
                }

                $.post('/deck/set_card', {id:id}, function () {
                    deck[id-1].ownerType = 2;
                    deck[id-1] = undefined;
                    $message.text('This card is now in your deck');
                });
            }
            updateCardStatusDiv(card, $('#' + id));
        });

        $('#bt_turn_into_gold').click(function () {
            var id = $('#ol_card').children('.card')[0].id;
            if (id <= 52) {
                $message.text('You may not turn starter cards into gold');
                return;
            }
            
            var card = findCardById(id);
            if (card.ownerType == 3) {
                $message.text('This card is in the market');
                return;
            }
            
            var str = $('#bt_turn_into_gold').val();
            var amount = parseInt(str.replace( /^\D+/g, ''));
            $('#ol_buttons').hide();
            $('#ol_confirm_buttons').show();
            $message.text('Turn this card into ' + amount + ' gold?');
            $('#bt_confirm').click(function () {
                $.post('card/turn_into_gold', {id:id}, function () {
                    deck = $.grep(deck, function(e){
                        return typeof(e) == 'undefined' || e.id != id;
                    });
                    collection = $.grep(collection, function(e){
                        return e.id != id;
                    });
                    collectionAndStarter = $.grep(collectionAndStarter, function(e){
                        return e.id != id;
                    });
                    $('#' + id).hide();
                    console.log(collection);
                    //TODO: display gold
                    $('#overlay').hide();
                    updateCardStatusDiv(card, $('#' + id));
                });
                $('#bt_confirm').off();
            });
        });

        $('#bt_sell').click(function () {
            var id = $('#ol_card').children('.card')[0].id;
            if (id <= 52) {
                $message.text('You may not sell starter cards');
                return;
            }

            var card = findCardById(id);
            if (card.ownerType == 3) {
                $message.text('This card is already in the market');
                return;
            }

            $('#ol_buttons').hide();
            $('#ol_selling').show();
            $message.text('Put this card for sale on the market');

            $('#bt_confirm').off();
            $('#bt_confirm_sell').click(function () {
                var input = parseInt($('#price').val());
                if (input > 9999 || input <= 0 || isNaN(input)) {
                    $message.text('Price can only be within 1~9999');
                    return;
                }

                $('#ol_selling').hide();
                $('#ol_confirm_buttons').show();
                $message.text('Sell on the market for ' + input + ' gold?');
                $('#bt_confirm').click(function () {
                    $.post('market/new_offer', {id:id, price:input}, function () {
                        card.ownerType = 3;
                        updateCardStatusDiv(card, $('#' + id));
                        $('#overlay').hide();
                    });
                    $('#bt_confirm').off();
                });
            });
        });
    });
});

$(document).mouseup(function(e) {
    var container = $('#overlay');
    if (!container.is(e.target) && container.has(e.target).length === 0 && !$('.card').is(e.target) && $('.card').has(e.target).length === 0)
    {
        container.hide();
    }
});

function findCardById(id) {
    var card = undefined;
    if (parseInt(id) > 52) {
        for (var i = 0; i < collection.length && typeof(card) == 'undefined'; i++)
            if (collection[i].id == id)
                card = collection[i];
    }
    else
        card = starter[id-1];
    return card;
}

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

function displayCards(cards, div) {
    div.empty();
    cards.forEach(function (card) {
        var cardDiv = addCardBody(card, div);
        var $statusDiv = $('<div class="card_status">').appendTo(cardDiv);
        updateCardStatusDiv(card, cardDiv);
    });
}

function updateCardStatusDiv(card, cardDiv) {
    var statusDiv = $(cardDiv).children('.card_status')[0];
    switch (parseInt(card.ownerType)) {
        case 0:
            if (typeof(deck[card.indecks]) == 'undefined') {
                $(statusDiv).text('In Use');
                $(statusDiv).css({'background-color':'rgba(153, 204, 255, 32)'});
            }
            break;
        case 1:
            $(statusDiv).text('In Use');
            $(statusDiv).css({'background-color':'rgba(153, 204, 255, 32)'});
            break;
        case 3:
            $(statusDiv).text('For Sale');
            $(statusDiv).css({'background-color':'rgba(255, 255, 153, 128)'});
            break;
        default:
            $(statusDiv).empty();
            $(statusDiv).css({'background-color':'transparent'});
            break;
    }
}