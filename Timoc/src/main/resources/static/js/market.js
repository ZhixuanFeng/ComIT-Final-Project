"use strict"

var gold;
var numOfCards = 0;
var maxNumOfCards = 52;
$(document).ready(function()
{
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    var offersDiv = $('.offers');
    $.post('market/all', function (result) {
        displayCards(result, offersDiv);
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

        $.post('market/filter', criteria, function (result) {
            displayCards(result, offersDiv);
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