"use strict"

$(document).ready(function()
{
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    $.post('market/all', function (result) {
        console.log(result);
    });

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

        console.log(criteria);
        $.post('market/filter', criteria, function (result) {
            console.log(result);
        /*}).fail(function() {
            window.location.replace('error');*/
        });
    });
});