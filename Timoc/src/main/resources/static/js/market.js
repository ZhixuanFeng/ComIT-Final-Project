"use strict"

var gold;
var numOfCards = 0;
var maxNumOfCards = 52;
var activePageNum = 0;
$(document).ready(function()
{
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    var cardAreaDiv = $('#card_area');
    $.post('market/all', {page:0}, function (result) {
        displayCards(result.content, cardAreaDiv);

        var totalPages = result.totalPages;
        $('#page_numbers').empty();

        prevOrNextPageBt('market/all', {page:0}, false, totalPages);
        for (var i = 0; i < totalPages && i < 6; i++) {
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

    var effectCheckboxes = [$('#cb_attack'), $('#cb_block'), $('#cb_heal'), $('#cb_mana'), $('#cb_aoe'), $('#cb_draw'), $('#cb_revive'), $('#cb_taunt')];
    var suitCheckboxes = [$('#cb_diamond'), $('#cb_club'), $('#cb_heart'), $('#cb_spade')];
    var rankCheckboxes = [$('#cb_1'), $('#cb_2'), $('#cb_3'), $('#cb_4'), $('#cb_5'), $('#cb_6'), $('#cb_7'), $('#cb_8'), $('#cb_9'), $('#cb_10'), $('#cb_11'), $('#cb_12'), $('#cb_13')];

    var anyEffectCB = $('#cb_effect_any');
    var allSuitCB = $('#cb_suit_all');
    var allRankCB = $('#cb_rank_all');

    var nEffectCBChecked = 0;
    var nSuitCBChecked = 4;
    var nRankCBChecked = 13;

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
        var criteria = {};

        if (nEffectCBChecked > 0) {
            criteria['effect'] = '';
            effectCheckboxes.forEach(function (cb) {
                if (cb.is(':checked'))
                    criteria['effect'] += cb.val() + '&';
            });
            var str = criteria['effect'];
            criteria['effect'] = str.substr(0, str.length-1); // remove the last '&'
        }

        // instead of returning no results, no checkbox checked has the same meaning as all checkboxes checked
        if (nSuitCBChecked != 0 && nSuitCBChecked != 4) {
            criteria['suit'] = '';
                suitCheckboxes.forEach(function (cb) {
                if (cb.is(':checked'))
                    criteria['suit'] += cb.val() + '|';
            });
            var str = criteria['suit'];
            criteria['suit'] = str.substr(0, str.length-1);
        }

        if (nRankCBChecked != 0 && nRankCBChecked != 13) {
            criteria['rank'] = '';
            rankCheckboxes.forEach(function (cb) {
                if (cb.is(':checked'))
                    criteria['rank'] += cb.val() + '|';
            });
            var str = criteria['rank'];
            criteria['rank'] = str.substr(0, str.length-1);
        }

        criteria['page'] = 0;

        $.post('market/filter', criteria, function (result) {
            displayCards(result.content, cardAreaDiv);

            var totalPages = result.totalPages;
            $('#page_numbers').empty();
            prevOrNextPageBt('market/filter', criteria, false, totalPages);
            for (var i = 0; i < totalPages && i < 6; i++) {
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
    var $overlay = $('#overlay');
    cards.forEach(function (offer) {
        var cardDiv = addCardBody(offer, div);
        var $price = $('<div>').addClass('price').appendTo(cardDiv);
        var $goldIcon = $('<img src="images/gold.png" height="8" width="8"/>').addClass('gold_icon').appendTo($price);
        var $priceNum = $('<div>').text(offer.price).addClass('price_num').appendTo($price);

        $(cardDiv).click(function () {
            if ($overlay.is(':visible')) {
                $overlay.hide();
                return;
            }

            $($overlay).show();
            displayCards([offer], $('#ol_card'));
            $('#ol_sold_by').text('Sold by: ' + offer.playerName);
            $('#bt_buy').show();
            $('#bt_dismiss').show();

            $('#bt_buy').click(function () {
                if (gold < offer.price) {
                    $('#message').text('Not enough gold').css('color', 'red');
                }
                else if (numOfCards >= maxNumOfCards) {
                    $('#message').text('Your storage is full').css('color', 'red');
                }
                else {
                    $.post('market/accept/offer_id', {id: offer.id}, function () {
                        $(cardDiv).hide();
                        gold -= offer.price;
                        updateGold();
                        $overlay.hide();
                    });
                }
            });
        });
    });

    $('#bt_dismiss').click(function () {
        $('#overlay').hide();
    });

    $(document).mouseup(function(e) {
        var container = $('#overlay');
        if (container.is(':visible') && !container.is(e.target) && container.has(e.target).length === 0 && !$('.card').is(e.target) && $('.card').has(e.target).length === 0)
        {
            container.hide();
        }
    });

    function updateGold() {

    }
}

function newPageNumBt(pageNum, postURL, postData, totalPages) {
    var $page = $('<a href="#">');
    $page.text(pageNum+1);
    if (pageNum == activePageNum) {
        $page.addClass('active_page');
    }

    $page.click(function () {
        var page = this;
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
    var $bt = $('<a href="#">');

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
        var start = activePageNum - 1;
        var end = activePageNum + 4;
        if (start < 1) {
            start = 1;
            end = 6;
        }
        else if (end > totalPages) {
            end = totalPages;
            start = end - 5;
        }
        var pageNums = $('#page_numbers').children();
        for (var i = 1; i <= 6; i++) {
            $(pageNums[i]).text(start + i - 1);
            if (start + i - 2 == activePageNum) {
                $('.active_page').removeClass('active_page');
                $(pageNums[i]).addClass('active_page');
            }
        }
    }
    else {
        if ($(clicked).text() === '<') {
            var active = $('.active_page');
            $('.active_page').removeClass('active_page');
            active.prev().addClass('active_page');
        }
        else if ($(clicked).text() === '>') {
            var active = $('.active_page');
            $('.active_page').removeClass('active_page');
            active.next().addClass('active_page');
        }
        else {
            $('.active_page').removeClass('active_page');
            $(clicked).addClass('active_page');
        }
    }
}