"use strict";

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