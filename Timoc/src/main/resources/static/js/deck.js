"use strict";

var cardsInStorage = [];
var deck = [new Array(13), new Array(13), new Array(13), new Array(13)];
var currentSuit = 0;

$(document).ready(function()
{
    $.getJSON("/rest/cards", function (json) {
        // find cards in deck
        json.forEach(function (card) {
            if (card.ownerType == 1)
                deck[getSuit(card)][getRank(card) - 1] = card;
            else
                cardsInStorage.push(card);
        });

        // major divs
        var $suitFilterDiv = $('<div class="suit_filter">').appendTo('body');
        var $diamondBtn = $('<img src="images/diamond.png"/>');
        var $clubBtn = $('<img src="images/club.png"/>');
        var $heartBtn = $('<img src="images/heart.png"/>');
        var $spadeBtn = $('<img src="images/spade.png"/>');
        var suitBtns = [$diamondBtn, $clubBtn, $heartBtn, $spadeBtn];
        var $deckArea = $('<div>').addClass('card_area').appendTo('body');
        var $storageArea = $('<div>').addClass('card_area').addClass('storage_area').appendTo('body');

        // suit button onclick
        for (var i = 0; i < 4; i++)
        {
            suitBtns[i].attr('id', i).addClass('suit_btn').appendTo($suitFilterDiv);
            suitBtns[i].click(function () {
                $deckArea.empty();
                $storageArea.empty();
                currentSuit = this.id;
                setCardArea();
            });
        }

        // allow wheel scroll horizontally
        $('.card_area').mousewheel(function(e, delta) {
            this.scrollLeft -= (delta * 100);
            e.preventDefault();
        });

        setCardArea();

        function setCardArea() {
            displayCards(deck[currentSuit], $deckArea);
            for (var i = 0; i < 13; i++) {
                var indecks = currentSuit * 13 + i;
                var n = 0;
                if (typeof(deck[currentSuit][i]) != 'undefined' ) n++;
                cardsInStorage.forEach(function (cardInStorage) {
                    if (cardInStorage.indecks == indecks) n++;
                });
                $($($deckArea).children().eq(i)).children('div.num').text(n);
            };

            var isClick = false;
            $deckArea.children('.card').mousedown(function () {
                isClick = true;
            });
            $deckArea.children('.card').mousemove(function () {
                isClick = isClick ? false : false;
            });
            $deckArea.children('.card').mouseup(function () {
                if (!isClick) return;
                $storageArea.empty();
                var rank = $(this).children('span.rank').text();
                if (rank == 'A') rank = 1;
                else if (rank == 'J') rank = 11;
                else if (rank == 'Q') rank = 12;
                else if (rank == 'K') rank = 13;
                else rank = parseInt(rank);
                var indecks = currentSuit * 13 + rank - 1;
                var cardChoices = [];
                cardsInStorage.forEach(function (card) {
                    if (card.indecks == indecks) cardChoices.push(card);
                });
                if (typeof(deck[currentSuit][rank-1]) != 'undefined')
                    cardChoices.push(starter[indecks]);
                displayCards(cardChoices, $storageArea);

                $storageArea.children('.card').click(function () {
                    var id = this.id;
                    $.get('/deck/set_card', {id:id}, function () {
                            $storageArea.empty();
                            $deckArea.empty();
                            var chosen;
                            cardChoices.forEach(function (card) {
                                if (card.id == id) chosen = card;
                            });
                            var index = cardsInStorage.indexOf(chosen);
                            if (index > -1) { // picking non-starter
                                cardsInStorage.splice(index, 1);
                                if (typeof(deck[currentSuit][getRank(chosen) - 1]) != 'undefined')
                                    cardsInStorage.push(deck[currentSuit][getRank(chosen) - 1]);
                                deck[currentSuit][getRank(chosen) - 1] = chosen;
                            }
                            else {
                                cardsInStorage.push(deck[currentSuit][getRank(chosen) - 1]);
                                deck[currentSuit][getRank(chosen) - 1] = undefined;
                            }
                            setCardArea();
                    }).fail(function() {
                        window.location.replace('error');
                    });
                });
            });
        }
    });
});

function displayCards(cards, $div) {
    for (var i = 0; i < cards.length; i++) {
        var card = (typeof(cards[i]) == 'undefined') ? starter[currentSuit * 13 + i] : cards[i];
        var $cardDiv = $('<div>');
        var $emptyCardImg = $('<img src="images/empty_card.png" height="61" width="41"/>');
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

        var $num = $('<div>').addClass('num').appendTo($cardDiv);
    }
    $($div).dragscrollable({
        dragSelector: '.card'
    });

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

function printCards() {
    console.log(cardsInDeck);
}

function getRank(card) {
    return card.indecks - getSuit(card) * 13 + 1;
}

function getSuit(card) {
    return Math.floor(card.indecks / 13);
}