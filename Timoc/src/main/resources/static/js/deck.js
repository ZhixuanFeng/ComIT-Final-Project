var allCards;
var cardsInDeck = [];

$(document).ready(function()
{
    $.getJSON("/rest/cards", function (json) {
        allCards = json;

        // find cards in deck
        allCards.forEach(function (card) {
            if (card.ownerType == 1)
                cardsInDeck[card.indecks] = card;
        });

        displayCards();

        printCards();
    });

    $(".card").click(function () {
    });
});

function displayCards() {
    var $deckDiv = $('#deck');
    // fill empty slots with starter cards
    for (var i = 0; i < 52; i++) {
        var card = (typeof(cardsInDeck[i]) == 'undefined') ? starter[i] : cardsInDeck[i];
        var $cardDiv = $('<div class="card">');
        var $emptyCardImg = $('<img class="empty_card" src="images/empty_card.png" height="61" width="41"/>');
        var $rankSpan = $('<span class="rank"></span>');
        var $suitImg = $('<img class="suit" height="11" width="11"/>');
        var $cardEffectDiv = $('<div class="card_effect">');
        var rank = getRank(card);
        var suit = getSuit(card);
        $cardDiv.attr('id', card.indecks);
        $rankSpan.text(rank == 1 ? 'A' : rank == 11 ? 'J' : rank == 12 ? 'Q' : rank == 13 ? 'K' : rank);
        $rankSpan.attr('style', suit == 0 || suit == 2 ? 'color: red' : 'color:black');
        $suitImg.attr('src', suit == 0 ? 'images/diamond.png' : suit == 1 ? 'images/club.png' : suit == 2 ? 'images/heart.png' : 'images/spade.png');
        if (card.attack > 0) addCardEffect('attack', card.attack, suit);
        if (card.block > 0) addCardEffect('block', card.block, suit);
        if (card.heal > 0) addCardEffect('heal', card.heal, suit);
        if (card.mana > 0) addCardEffect('mana', card.mana, suit);
        if (card.aoe > 0) addCardEffect('aoe', card.aoe, suit);
        if (card.random > 0) addCardEffect('random', card.random, suit);
        if (card.revive > 0) addCardEffect('revive', card.revive, suit);
        if (card.taunt > 0) addCardEffect('taunt', card.taunt, suit);

        $cardDiv.addClass('card').appendTo($deckDiv);
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

function printCards() {
    console.log(cardsInDeck);
}

function getRank(card) {
    return card.indecks - getSuit(card) * 13 + 1;
}

function getSuit(card) {
    return Math.floor(card.indecks / 13);
}