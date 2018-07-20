"use strict";

var cardsInStorage = [];
var deck = [new Array(13), new Array(13), new Array(13), new Array(13)];
var currentSuit = 0;

$(document).ready(function()
{
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    $.getJSON("/rest/cards", function (json) {
        // find cards the player owns
        json.forEach(function (card) {
            if (card.ownerType == 1)
                deck[getSuit(card)][getRank(card) - 1] = card;
            else if (card.ownerType == 2)
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

            $deckArea.children('.card').on('mousedown', function(e) {
                $(this).data('p0', { x: e.pageX, y: e.pageY });
            }).on('mouseup', function(e) {
                var p0 = $(this).data('p0');
                var p1 = { x: e.pageX, y: e.pageY };
                var d = Math.sqrt(Math.pow(p1.x - p0.x, 2) + Math.pow(p1.y - p0.y, 2));

                if (d < 4) { // on click
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
                        $.post('/deck/set_card', {id:id}, function () {
                            $storageArea.empty();
                            $deckArea.empty();
                            var chosen;
                            cardChoices.forEach(function (card) {
                                if (card.id == id) chosen = card;
                            });
                            var index = cardsInStorage.indexOf(chosen);
                            if (index > -1) { // picking non-starter
                                cardsInStorage.splice(index, 1);
                                if (typeof(deck[currentSuit][getRank(chosen) - 1]) != 'undefined') {
                                    deck[currentSuit][getRank(chosen) - 1].ownerType = 2;
                                    cardsInStorage.push(deck[currentSuit][getRank(chosen) - 1]);
                                }
                                chosen.ownerType = 1;
                                deck[currentSuit][getRank(chosen) - 1] = chosen;
                            }
                            else {
                                deck[currentSuit][getRank(chosen) - 1].ownerType = 2;
                                cardsInStorage.push(deck[currentSuit][getRank(chosen) - 1]);
                                deck[currentSuit][getRank(chosen) - 1] = undefined;
                            }
                            setCardArea();
                        }).fail(function() {
                            window.location.replace('error');
                        });
                    });
                }
            });
        }
    });
});

function displayCards(cards, $div) {
    for (var i = 0; i < cards.length; i++) {
        var card = (typeof(cards[i]) == 'undefined') ? starter[currentSuit * 13 + i] : cards[i];
        var $cardDiv = addCardBody(card, $div);

        var $num = $('<div>').addClass('num').appendTo($cardDiv);
    }
    $($div).dragscrollable({
        dragSelector: '.card'
    });
}