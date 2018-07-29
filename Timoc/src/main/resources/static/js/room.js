"use strict";

let roomCode;
let players = [];
$(document).ready(function() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    let $overlay = $('#overlay');
    let $readyBt = $('#ready_bt');
    $('#player_list').hide();
    $readyBt.hide();
    $overlay.show();

    $('#ol_create').click(function () {
        $.post('room/create', function (code) {
            enterRoom(code);
        });
    });

    let $input = $('#ol_code_input');
    $input.on('input', function(e){
        let code = $input.val().toUpperCase();
        $input.val(code);
        if (code.length == 4) {
            enterRoom(code);
        }
        else {
            $('#ol_message').text('');
        }
    });

    $readyBt.click(function () {
        sendMessage('/app/room.ready/' + roomCode, {}); // toggle
    });
});

let stompClient = null;

function connect(code) {
    let socket = new SockJS('/room');
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
        let $overlay = $('#overlay');
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
    let messageBody = JSON.parse(message.body);
    if (messageBody.type === 'Info') {
        let newPlayers = findNewPlayers(messageBody.players);
        displayPlayers(newPlayers);
    }
    else if (messageBody.type === 'Chat') {

    }
    else if (messageBody.type === 'Leave') {
        removePlayer(messageBody.name);
    }
    else if (messageBody.type === 'Ready') {
        setPlayerReady(messageBody.name, messageBody.ready);
    }
}

function sendMessage(destination, data) {
    stompClient.send(destination, {}, data);
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
    $('#ready_bt').show();
    $('#overlay').hide();
    $('#room_code').text(roomCode);
}

function findNewPlayers(playerInfoes) {
    let newPlayers = [];
    // find the ones not already displayed
    playerInfoes.forEach(function (playerInfo) {
        let exists = false;
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
    let availableDivs = [];
    $('.playerDiv').toArray().forEach(function (div) {
        if (!$(div).hasClass('occupied')) {
            availableDivs.push(div);
        }
    });
    for (let i = 0; i < player.length; i++) {
        let $player = $('<div>').addClass('player').appendTo(availableDivs[i]);
        $('<div>').text(player[i].name).addClass('name').appendTo($player);
        $('<img src="images/player.png" height="32" width="32"/>').addClass('avatar').appendTo($player);
        $('<div>').text('Ready').addClass('ready').hide().appendTo($player);
        $(availableDivs[i]).addClass('occupied');
        $(availableDivs[i]).attr('id', player[i].name);
        players.push(player[i]);
    }
}

function removePlayer(name) {
    let playerDiv = $('#' + name);
    $(playerDiv).empty();
    $(playerDiv).removeClass('occupied');
    $(playerDiv).removeAttr('id');
    players = $.grep(players, function (e) {
        return e.name !== name;
    });
}

function setPlayerReady(name, isReady) {
    let $playerDiv = $('#' + name);
    isReady ? $playerDiv.find('.ready').show() : $playerDiv.find('.ready').hide();
}