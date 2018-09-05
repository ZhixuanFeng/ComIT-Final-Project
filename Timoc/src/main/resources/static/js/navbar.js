"use strict";

let myInfo;
$(document).ready(function() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    updateNavBar();
});

function updateNavBar() {
    $.post('userInfo/myInfo', function (result) {
        myInfo = result;
        $('#username').text(myInfo.name);
        $('#gold_amount').text(myInfo.gold);
        $('#card_amount').text(myInfo.cardsOwned);
    });
}