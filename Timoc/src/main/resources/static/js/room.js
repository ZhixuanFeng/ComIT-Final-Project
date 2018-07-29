"use strict";

var roomCode;
var players = [];
$(document).ready(function() {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    var $overlay = $('#overlay');
    $('#player_list').hide();
    $('#start_bt').hide();
    $overlay.show();

    $('#ol_create').click(function () {
        $.post('room/create', function (code) {
            enterRoom(code);
        });
    });

    var $input = $('#ol_code_input');
    $input.on('input', function(e){
        var code = $input.val().toUpperCase();
        $input.val(code);
        if (code.length == 4) {
            enterRoom(code);
        }
        else {
            $('#ol_message').text('');
        }
    });
});

var stompClient = null;

function connect(code) {
    var socket = new SockJS('/room');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/room/' + code, onMessageReceived);
        stompClient.send('/app/room.enter/' + code, {}, code);
        hideOverlay(code);
    }, onError);
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
}

function onError(error) {
    if (typeof(error) === "string" && error.indexOf('Whoops!') !== -1) {
        var $overlay = $('#overlay');
        $overlay.empty();
        $('<div>').text('Connection Lost. Please refresh the page.').appendTo($overlay);
        $overlay.show();
        return;  // lost connection
    }
    if ($('#ol_message').is(':visible')) {
        $('#ol_message').text(error.headers.message);
    }
    // disconnect();
}

function onMessageReceived(message) {
    var messageBody = JSON.parse(message.body);
    if (messageBody.type === 'Info') {
        var newPlayers = findNewPlayers(messageBody.players);
        displayPlayers(newPlayers);
    }
    else if (messageBody.type === 'Chat') {

    }
    else if (messageBody.type === 'Leave') {
        removePlayer(messageBody.name);
    }
}

function sendMessage() {
    stompClient.send("/room", {}, {});
}

function enterRoom(code) {
    $.post('room/enter', {code: code}, function (response) {
        if (response === 'OK') {
            connect(code);
            roomCode = code;
        }
        else {
            if ($('#ol_message').is(':visible')) {
                $('#ol_message').text(response);
            }
        }
    });
}

function hideOverlay(roomCode) {
    $('#player_list').show();
    $('#start_bt').show();
    $('#overlay').hide();
    $('#room_code').text(roomCode);
}

function findNewPlayers(playerInfoes) {
    var newPlayers = [];
    // find the ones not already displayed
    playerInfoes.forEach(function (playerInfo) {
        var exists = false;
        players.forEach(function (player) {
            if (player.name === playerInfo.name) {
                exists = true;
            }
        });
        if (!exists) {
            newPlayers.push(playerInfo);
        }
    });
    return newPlayers;
}

function displayPlayers(player) {
    var availableDivs = [];
    $('.playerDiv').toArray().forEach(function (div) {
        if (!$(div).hasClass('occupied')) {
            availableDivs.push(div);
        }
    });
    for (var i = 0; i < player.length; i++) {
        var $player = $('<div>').addClass('player').appendTo(availableDivs[i]);
        $('<div>').text(player[i].name).addClass('name').appendTo($player);
        $('<img src="images/player.png" height="32" width="32"/>').addClass('avatar').appendTo($player);
        $(availableDivs[i]).addClass('occupied');
        players.push(player[i]);
    }
}

function removePlayer(name) {
    $('.playerDiv').toArray().forEach(function (div) {
        if ($($(div).find('.name')[0]).text() === name) {
            $(div).empty();
            $(div).removeClass('occupied');
            players = $.grep(players, function (e) {
                return e.name !== name;
            });
        }
    });
}