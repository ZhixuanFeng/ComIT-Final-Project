"use strict"

function toTop() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}

function onMenuClicked() {
    toTop();
    let nav = document.getElementById("topnav");
    if (nav.className === "topnav navbar-fixed-top") {
        nav.className += " menuClicked";
    } else {
        nav.className = "topnav navbar-fixed-top";
    }
}

window.onload = function() {
    if (screen.width <= 768) {
        let fltPanel = document.getElementById("filterPanel");
        fltPanel.className += " collapse";

        document.getElementById("effect_cbs").className += " collapse";
        document.getElementById("suit_cbs").className += " collapse";
        document.getElementById("rank_cbs").className += " collapse";
    }

    let target = (width <= 768) ? '#filterPanel' : '#';
    $('.apply').resize().each(function () {
        $(this).attr('data-target', target);
    });
};

function cbTitleOnClick(id) {
    let chevron = document.getElementById(id);
    chevron.className = chevron.className === "glyphicon glyphicon-chevron-right" ? "glyphicon glyphicon-chevron-down" : "glyphicon glyphicon-chevron-right";
}

let gold;
let numOfCards = 0;
let maxNumOfCards = 52;
let activePageNum = 0;
$(document).ready(function()
{
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    let cardAreaDiv = $('#card_area');
    $.post('market/all', {page:0}, function (result) {
        displayCards(result.content, cardAreaDiv);

        let totalPages = result.totalPages;
        $('#page_numbers').empty();

        prevOrNextPageBt('market/all', {page:0}, false, totalPages);
        for (let i = 0; i < totalPages && i < 6; i++) {
            newPageNumBt(i, 'market/all', {page:i}, totalPages);
        }
        prevOrNextPageBt('market/all', {page:0}, true, totalPages);
    });

    /*$.post('player/gold', function (result) {
        gold = parseInt(result);
    });

    $.post('player/number_of_cards', function (result) {
        gold = parseInt(result);
    });*/
    // $('#bt_buy').hide();
    // $('#bt_dismiss').hide();

    let effectCheckboxes = [$('#cb_attack'), $('#cb_block'), $('#cb_heal'), $('#cb_mana'), $('#cb_aoe'), $('#cb_draw'), $('#cb_revive'), $('#cb_taunt')];
    let suitCheckboxes = [$('#cb_diamond'), $('#cb_club'), $('#cb_heart'), $('#cb_spade')];
    let rankCheckboxes = [$('#cb_1'), $('#cb_2'), $('#cb_3'), $('#cb_4'), $('#cb_5'), $('#cb_6'), $('#cb_7'), $('#cb_8'), $('#cb_9'), $('#cb_10'), $('#cb_11'), $('#cb_12'), $('#cb_13')];

    let anyEffectCB = $('#cb_effect_any');
    let allSuitCB = $('#cb_suit_all');
    let allRankCB = $('#cb_rank_all');

    let nEffectCBChecked = 0;
    let nSuitCBChecked = 4;
    let nRankCBChecked = 13;

    anyEffectCB.change(function () {
        effectCheckboxes.forEach(function (cb) {
            if (anyEffectCB.is(':checked')) {
                cb.prop('checked', false);
                nEffectCBChecked = 0;
            }
            else {
                anyEffectCB.prop('checked', true);
                nEffectCBChecked = 0;
            }
        });
    });
    // when "all" checkbox is checked or unchecked, all checkboxes below should follow
    allSuitCB.change(function () {
        suitCheckboxes.forEach(function (cb) {
            cb.prop('checked', allSuitCB.is(':checked'));
            if (allSuitCB.is(':checked'))
                nSuitCBChecked = 4;
            else
                nSuitCBChecked = 0;
        });
    });
    allRankCB.change(function () {
        rankCheckboxes.forEach(function (cb) {
            cb.prop('checked', allRankCB.is(':checked'));
            if (allRankCB.is(':checked'))
                nRankCBChecked = 13;
            else
                nRankCBChecked = 0;
        });
    });

    // when a checkbox is unchecked, "all" checkbox should be unchecked
    effectCheckboxes.forEach(function (cb) {
        cb.change(function () {
            if (cb.is(':checked')) {
                if (nEffectCBChecked == 4){

                    cb.prop('checked', false);
                }
                else {
                    nEffectCBChecked++;
                    if (anyEffectCB.is(':checked'))
                        anyEffectCB.prop('checked', false);
                }
            }
            else {
                nEffectCBChecked--;
                if (nEffectCBChecked == 0)
                    anyEffectCB.prop('checked', true);
            }
        });
    });
    suitCheckboxes.forEach(function (cb) {
        cb.change(function () {
            if (cb.is(':checked')){
                nSuitCBChecked++;
                if (nSuitCBChecked == 4)
                    allSuitCB.prop('checked', true);
            }
            else {
                nSuitCBChecked--;
                allSuitCB.prop('checked', false);
            }
        });
    });
    rankCheckboxes.forEach(function (cb) {
        cb.change(function () {
            if (cb.is(':checked')){
                nRankCBChecked++;
                if (nRankCBChecked == 13)
                    allRankCB.prop('checked', true);
            }
            else {
                nRankCBChecked--;
                allRankCB.prop('checked', false);
            }
        });
    });

    $('.apply').click(function () {
        let criteria = {};

        if (nEffectCBChecked > 0) {
            criteria['effect'] = '';
            effectCheckboxes.forEach(function (cb) {
                if (cb.is(':checked'))
                    criteria['effect'] += cb.val() + '&';
            });
            let str = criteria['effect'];
            criteria['effect'] = str.substr(0, str.length-1); // remove the last '&'
        }

        // instead of returning no results, no checkbox checked has the same meaning as all checkboxes checked
        if (nSuitCBChecked != 0 && nSuitCBChecked != 4) {
            criteria['suit'] = '';
                suitCheckboxes.forEach(function (cb) {
                if (cb.is(':checked'))
                    criteria['suit'] += cb.val() + '|';
            });
            let str = criteria['suit'];
            criteria['suit'] = str.substr(0, str.length-1);
        }

        if (nRankCBChecked != 0 && nRankCBChecked != 13) {
            criteria['rank'] = '';
            rankCheckboxes.forEach(function (cb) {
                if (cb.is(':checked'))
                    criteria['rank'] += cb.val() + '|';
            });
            let str = criteria['rank'];
            criteria['rank'] = str.substr(0, str.length-1);
        }

        criteria['page'] = 0;

        $.post('market/filter', criteria, function (result) {
            displayCards(result.content, cardAreaDiv);

            let totalPages = result.totalPages;
            $('#page_numbers').empty();
            prevOrNextPageBt('market/filter', criteria, false, totalPages);
            for (let i = 0; i < totalPages && i < 6; i++) {
                newPageNumBt(i, 'market/filter', criteria, totalPages);
            }
            prevOrNextPageBt('market/filter', criteria, true, totalPages);
        }).fail(function() {
            window.location.replace('error');
        });
    });
});

function displayCards(cards, div) {
    div.empty();
    cards.forEach(function (offer) {
        let $container = $('<div>').addClass('col-xs-6 col-sm-4 col-md-3 col-lg-2').appendTo(div);
        let $cardDiv = addCardBody(offer, $container);
        let $overlay = $('<div>').addClass('overlay').appendTo($cardDiv);
        let $soldByText = $('<div>').addClass('seller').text('Seller: ' + offer.playerName).hide().appendTo($overlay);
        let $buyBtn = $('<button>').addClass('btn btn-warning').text('Buy').hide().appendTo($overlay);
        let $confirmText = $('<div>').addClass('confirm').text('Sure?').hide().appendTo($overlay);
        let $confirmBtn = $('<button>').addClass('btn btn-success').text('Yes').hide().appendTo($overlay);
        let $cancelBtn = $('<button>').addClass('btn btn-danger').text('No').hide().appendTo($overlay);
        let $price = $('<div>').addClass('price').appendTo($container);
        let $goldIcon = $('<img src="images/gold.png"/>').addClass('gold_icon').appendTo($price);
        let $priceNum = $('<div>').text(offer.price).addClass('price_num').appendTo($price);

        $cardDiv.mouseenter(resetButtons);
        $cardDiv.mouseleave(hideButtons);

        $buyBtn.click(function () {
            $soldByText.hide();
            $buyBtn.hide();
            $confirmText.show();
            $confirmBtn.show();
            $cancelBtn.show();
        });

        $cancelBtn.click(resetButtons);

        function hideButtons() {
            $soldByText.hide();
            $buyBtn.hide();
            $confirmText.hide();
            $confirmBtn.hide();
            $cancelBtn.hide();
        }

        function resetButtons() {
            $soldByText.show();
            $buyBtn.show();
            $confirmText.hide();
            $confirmBtn.hide();
            $cancelBtn.hide();
        }

        $confirmBtn.click(function () {
            if (gold < offer.price) {
                $('#message').text('Not enough gold').css('color', 'red');
            }
            else if (numOfCards >= maxNumOfCards) {
                $('#message').text('Your storage is full').css('color', 'red');
            }
            else {
                // $.post('market/accept/offer_id', {id: offer.id}, function () {
                //     $($cardDiv).hide();
                //     gold -= offer.price;
                //     // updateGold();
                //     $overlay.hide();
                // });
                console.log('confirm purchase');
            }
        });
    });

    resizeCardFonts();
    sizeOverlays();
}

function addCardBody(card, div) {
    let $cardDiv = $('<div>');
    let $emptyCardImg;
    if (card.id <= 52)
        $emptyCardImg = $('<img src="images/empty_card_starter.png"/>');
    else
        $emptyCardImg = $('<img src="images/empty_card.png"/>');
    let $rankDiv = $('<div>');
    let $suitImg = $('<img/>');
    let $cardEffectDiv = $('<div>');
    let rank = getRank(card);
    let suit = getSuit(card);
    $cardDiv.attr('id', card.id);
    $rankDiv.text(rank == 1 ? 'A' : rank == 11 ? 'J' : rank == 12 ? 'Q' : rank == 13 ? 'K' : rank);
    $rankDiv.attr('style', suit == 0 || suit == 2 ? 'color: red' : 'color:black');
    $suitImg.attr('src', suit == 0 ? 'images/diamond.png' : suit == 1 ? 'images/club.png' : suit == 2 ? 'images/heart.png' : 'images/spade.png');
    if (card.attack > 0) addCardEffect('attack', card.attack, suit, $cardEffectDiv);
    if (card.block > 0) addCardEffect('block', card.block, suit, $cardEffectDiv);
    if (card.heal > 0) addCardEffect('heal', card.heal, suit, $cardEffectDiv);
    if (card.mana > 0) addCardEffect('mana', card.mana, suit, $cardEffectDiv);
    if (card.aoe > 0) addCardEffect('aoe', card.aoe, suit, $cardEffectDiv);
    if (card.draw > 0) addCardEffect('draw', card.draw, suit, $cardEffectDiv);
    if (card.revive > 0) addCardEffect('revive', card.revive, suit, $cardEffectDiv);
    if (card.taunt > 0) addCardEffect('hate', card.taunt, suit, $cardEffectDiv);

    $cardDiv.addClass('card').appendTo(div);
    $emptyCardImg.addClass('empty_card').appendTo($cardDiv);
    $rankDiv.addClass('rank').appendTo($cardDiv);
    $suitImg.addClass('suit').appendTo($cardDiv);
    $cardEffectDiv.addClass('card_effect').appendTo($cardDiv);

    return $cardDiv;
}

function addCardEffect(name, amount, suit, cardEffectDiv) {
    let $newEffectDiv = $('<div />').appendTo(cardEffectDiv);
    let $effectIconImg = $('<img class="effect_icon"/>');
    let $effectAmoundSpan = $('<span class="effect_amount""></span>');
    $effectIconImg.attr('src', 'images/' + name + '.png');
    $effectAmoundSpan.text(amount);
    $effectAmoundSpan.attr('style', suit == 0 || suit == 2 ? 'color: red' : 'color:black');
    $effectIconImg.addClass('effect_icon').appendTo($newEffectDiv);
    $effectAmoundSpan.addClass('effect_amount').appendTo($newEffectDiv);
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
        let target = (width <= 768) ? '#filterPanel' : '#';
        $('.apply').resize().each(function () {
            $(this).attr('data-target', target);
        });
    }
});

function getRank(card) {
    return card.indecks - getSuit(card) * 13 + 1;
}

function getSuit(card) {
    return Math.floor(card.indecks / 13);
}

function newPageNumBt(pageNum, postURL, postData, totalPages) {
    let $page = $('<a href="#">');
    $page.text(pageNum+1);
    if (pageNum == activePageNum) {
        $page.addClass('active_page');
    }

    $page.click(function () {
        let page = this;
        activePageNum = $(page).text() - 1;
        postData.page = activePageNum;
        $.post(postURL, postData, function (result) {
            displayCards(result.content, $('#card_area'));
            updateActivePageBt(page, totalPages);
        });
    });
    $page.appendTo($('#page_numbers'));
    return $page;
}

function prevOrNextPageBt(postURL, postData, isNext, totalPages) {
    let $bt = $('<a href="#">');

    if (isNext) $bt.text('>');
    else $bt.text('<');

    $bt.click(function () {
        if (isNext && activePageNum < totalPages - 1) activePageNum++;
        else if (!isNext && activePageNum > 0) activePageNum--;
        else return;
        postData.page = activePageNum;
        $.post(postURL, postData, function (result) {
            displayCards(result.content, $('#card_area'));
            updateActivePageBt($bt, totalPages);
        });
    });
    $bt.appendTo($('#page_numbers'));
}

function updateActivePageBt(clicked, totalPages) {
    if (totalPages > 6) {
        let start = activePageNum - 1;
        let end = activePageNum + 4;
        if (start < 1) {
            start = 1;
            end = 6;
        }
        else if (end > totalPages) {
            end = totalPages;
            start = end - 5;
        }
        let pageNums = $('#page_numbers').children();
        for (let i = 1; i <= 6; i++) {
            $(pageNums[i]).text(start + i - 1);
            if (start + i - 2 == activePageNum) {
                $('.active_page').removeClass('active_page');
                $(pageNums[i]).addClass('active_page');
            }
        }
    }
    else {
        if ($(clicked).text() === '<') {
            let active = $('.active_page');
            $('.active_page').removeClass('active_page');
            active.prev().addClass('active_page');
        }
        else if ($(clicked).text() === '>') {
            let active = $('.active_page');
            $('.active_page').removeClass('active_page');
            active.next().addClass('active_page');
        }
        else {
            $('.active_page').removeClass('active_page');
            $(clicked).addClass('active_page');
        }
    }
}