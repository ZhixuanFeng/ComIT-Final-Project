"use strict";

window.onload = function() {
    if (screen.width <= 768) {
        let fltPanel = document.getElementById("filterPanel");
        fltPanel.className += " collapse";
    }
};

let collection = [];
let deck = [];
let collectionAndStarter = [];
let selectedCardId;
$(document).ready(function()
{
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    let $cardArea = $('#card_area');
    let $overlay = $('#overlay');
    let $message = $('#message');
    $.getJSON("/rest/cards", function (json) {
        // find cards the player owns
        json.forEach(function (card) {
            let index = 0;
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

        let $diamondCB = $('#cb_diamond');
        let $clubCB = $('#cb_club');
        let $heartCB = $('#cb_heart');
        let $spadeCB = $('#cb_spade');
        let $starterCB = $('#cb_starter');
        let $indeckCB = $('#cb_indeck');
        let $forsaleCB = $('#cb_forsale');
        let $idleCB = $('#cb_idle');
        $diamondCB.change(applyFilter);
        $clubCB.change(applyFilter);
        $heartCB.change(applyFilter);
        $spadeCB.change(applyFilter);
        $starterCB.change(applyFilter);
        $indeckCB.change(applyFilter);
        $forsaleCB.change(applyFilter);
        $idleCB.change(applyFilter);

        function applyFilter() {
            let results = [];
            results = ($starterCB.is(':checked')) ? collectionAndStarter : collection;
            let showIndeck = $indeckCB.is(':checked');
            let showforsale = $forsaleCB.is(':checked');
            let showIdle = $idleCB.is(':checked');
            let temp = [];
            results.forEach(function (card) {
                if ((card.ownerType == 1 && showIndeck)||
                    (card.ownerType == 3 && showforsale)||
                    (card.ownerType == 2 && showIdle)||
                    (card.ownerType == 0 && !deck[card.indecks] && showIndeck)||
                    (card.ownerType == 0 && deck[card.indecks] && showIdle))
                    temp.push(card);
            });
            results = temp;

            let showDiamond = $diamondCB.is(':checked');
            let showClub = $clubCB.is(':checked');
            let showHeart = $heartCB.is(':checked');
            let showSpade = $spadeCB.is(':checked');
            let temp2 = [];
            results.forEach(function (card) {
                let suit = getSuit(card);
                if ((suit == 0 && showDiamond)||
                    (suit == 1 && showClub)||
                    (suit == 2 && showHeart)||
                    (suit == 3 && showSpade))
                    temp2.push(card);
            });
            results = temp2;

            displayCards(results, $cardArea);
        }



        $('#bt_dismiss, #bt_cancel').click(function () {
            $('#overlay').hide();
        });

        $('#bt_add_to_deck').click(function () {
            let id = selectedCardId;
            let card = findCardById(id);
            if (id > 52) {
                if (card.ownerType == 3) {
                    $message.text('You may not use cards that are in the market');
                }

                if (typeof(deck[card.indecks]) != 'undefined' && deck[card.indecks].id == id) {
                    $message.text('This card is already in your deck');
                    return;
                }

                $.post('/deck/set_card', {id:id}, function () {
                    card.ownerType = 1;
                    let cardReplaced = deck[card.indecks];
                    deck[card.indecks] = card;
                    if (typeof(cardReplaced) != 'undefined') {
                        cardReplaced.ownerType = 2;
                        updateCardStatusDiv(cardReplaced, $('#' + deck[card.indecks].id));
                    }
                    else {
                        updateCardStatusDiv(starter[card.indecks], $('#' + (card.indecks + 1)));
                    }
                    updateCardStatusDiv(card, $('#' + id));
                    // updateCardStatusDiv(card, $('#ol_card').children('.card')[0]);
                    $message.text('This card is now in your deck');
                });
            }
            else { // starter card
                if (typeof(deck[id-1]) == 'undefined') {
                    $message.text('This card is already in your deck');
                    return;
                }

                $.post('/deck/set_card', {id:id}, function () {
                    let cardReplaced = deck[id-1];
                    cardReplaced.ownerType = 2;
                    deck[id-1] = undefined;
                    $message.text('This card is now in your deck');
                    updateCardStatusDiv(cardReplaced, $('#' + cardReplaced.id));
                    updateCardStatusDiv(card, $('#' + id));
                    // updateCardStatusDiv(card, $('#ol_card').children('.card')[0]);
                });
            }
        });

        $('#bt_turn_into_gold').click(function () {
            let id = selectedCardId;
            if (id <= 52) {
                $message.text('You may not turn starter cards into gold');
                return;
            }
            
            let card = findCardById(id);
            if (card.ownerType == 3) {
                $message.text('This card is in the market');
                return;
            }
            
            let str = $('#bt_turn_into_gold').val();
            let amount = parseInt(str.replace( /^\D+/g, ''));
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
                    updateNavBar();
                    $('#overlay').hide();
                    let indecks = card.indecks;
                    if (typeof(deck[indecks]) != 'undefined') {
                        deck[indecks] = undefined;
                        updateCardStatusDiv(starter[indecks], $('#' + (indecks + 1)));
                    }
                    // updateCardStatusDiv(card, $('#' + id));
                    $('#container' + id).hide();
                });
                $('#bt_confirm').off();
            });
        });

        $('#bt_sell').click(function () {
            let id = selectedCardId;
            if (id <= 52) {
                $message.text('You may not sell starter cards');
                return;
            }

            let card = findCardById(id);
            if (card.ownerType == 3) {
                $message.text('This card is already in the market');
                return;
            }

            $('#ol_buttons').hide();
            $('#ol_selling').show();
            $('#price').focus();
            $message.text('Put this card for sale on the market');

            $('#bt_confirm').off();
            $('#bt_confirm_sell').click(function () {
                let input = parseInt($('#price').val());
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
                        let indecks = card.indecks;
                        if (typeof(deck[indecks]) != 'undefined' && deck[indecks].id == id) {
                            deck[indecks] = undefined;
                            updateCardStatusDiv(starter[indecks], $('#' + (indecks + 1)));
                        }
                        $('#overlay').hide();
                    });
                    $('#bt_confirm').off();
                });
            });
        });
    });
});

$(document).mouseup(function(e) {
    let container = $('#overlay');
    if (!container.is(e.target) && container.has(e.target).length === 0 && !$('.card').is(e.target) && $('.card').has(e.target).length === 0)
    {
        container.hide();
    }
});

function findCardById(id) {
    let card = undefined;
    if (parseInt(id) > 52) {
        for (let i = 0; i < collection.length && typeof(card) == 'undefined'; i++)
            if (collection[i].id == id)
                card = collection[i];
    }
    else
        card = starter[id-1];
    return card;
}

function combineSortedCardPiles(cards1, cards2) {
    let cards = [];
    for (let i = 0; i < cards1.length; i++)
        cards[i] = cards1[i];

    let index = 0;
    for (let i = 0; i < cards2.length; i++) {
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
        let $container = $('<div>').addClass('cardContainer col-xs-4 col-sm-3 col-md-2 col-lg-2').appendTo(div);
        $container.attr('id', 'container' + card.id);
        let cardDiv = addCardBody(card, $container);
        let $statusDiv = $('<div class="card_status">').appendTo($container);
        $statusDiv.attr('id', 'status' + card.id);
        updateCardStatusDiv(card, cardDiv);
    });

    resizeCardFonts();
    sizeOverlays();

    let $overlay = $('#overlay');
    let $message = $('#message');
    // show overlay on click
    $('.card').click(function () {
        if ($overlay.is(':visible')) {
            $overlay.hide();
            return;
        }

        selectedCardId = this.id;
        let id = this.id;
        let card = findCardById(id);
        $overlay.show();
        $('#ol_buttons').show();
        $('#ol_confirm_buttons').hide();
        $('#ol_selling').hide();

        $message.empty();

        if (id <= 52) {
            // $message.text('This is a starter card');
            $('#bt_add_to_deck').show();
            $('#bt_turn_into_gold').show();
            $('#bt_sell').show();
            $('#bt_cancel_offer').hide();
        }
        else {
            if (card.ownerType == 3) {
                $('#bt_add_to_deck').hide();
                $('#bt_turn_into_gold').hide();
                $('#bt_sell').hide();
                $message.text('Retrieving information from server..');
                $.post('market/card_id', {id:parseInt(id)}, function (offer) {
                    let offerId = offer.id;
                    let price = offer.price;
                    if (typeof(price) == 'undefined') {
                        $message.text('Cannot find this card in the market, please refresh the web page');
                    }
                    else {
                        $message.text('You are selling this card on the market for ' + price + ' gold');
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
                $('#bt_turn_into_gold').val((50 + card.quality) + ' gold');
            }
        }
    });
}

function resizeCardFonts() {
    let rankSize = $(document).find('.suit').outerWidth();
    $('.rank').resize().each(function () {
        $(this).css('font-size', rankSize + 'px');
    });
    let effectSize = $(document).find('.effect_icon').outerWidth() * 0.75;
    $('.effect_amount').resize().each(function () {
        $(this).css('font-size', effectSize + 'px');
    });
}

let sizeOverlays = function() {
    let w = $(document).find('.empty_card').outerWidth();
    let h = w / 41 * 61;
    $(".overlay").resize().each(function() {
        $(this).css('width', w);
        $(this).css('height', h);
        $(this).css('left', $(this).position());
    });
};

let width = $(window).width();
$(window).resize(function(){
    if($(this).width() !== width){
        width = $(this).width();
        resizeCardFonts();
        sizeOverlays();
        if (width > 768) $('#filterPanel').collapse('show');
    }
});

function updateCardStatusDiv(card, cardDiv) {
    // let statusDiv = $(cardDiv).children('.card_status')[0];
    let statusDiv = $('#status' + card.id);
    switch (parseInt(card.ownerType)) {
        case 0:
            if (typeof(deck[card.indecks]) === 'undefined')
                setCardStatusLabel(statusDiv, 'In Deck');
            else
                setCardStatusLabel(statusDiv, 'empty');
            break;
        case 1:
            setCardStatusLabel(statusDiv, 'In Deck');
            break;
        case 2:
            setCardStatusLabel(statusDiv, 'Idle Card');
            break;
        case 3:
            setCardStatusLabel(statusDiv, 'For Sale');
            break;
        default:
            setCardStatusLabel(statusDiv, 'empty');
            break;
    }
}

function setCardStatusLabel(div, status) {
    switch (status) {
        case 'In Deck':
            $(div).text('In Deck');
            $(div).css({'background-color': 'rgba(153, 204, 255, 32)', 'color':'rgba(0, 0, 0, 1)'});
            break;
        case 'For Sale':
            $(div).text('For Sale');
            $(div).css({'background-color':'rgba(255, 255, 153, 128)', 'color':'rgba(0, 0, 0, 1)'});
            break;
        default :
            $(div).text('Idle Card');
            $(div).css({'background-color':'transparent', 'color':'rgba(255, 255, 255, 0.0)'});
            break;
    }
}