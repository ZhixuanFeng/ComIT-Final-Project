"use strict";

let roomCode;
let playerMap = {};
let myInfo;
let myPosition;

let knightImage = new Image();
let wizardImage = new Image();
let berserkerImage = new Image();
let priestImage = new Image();
knightImage.src = 'images/knight.png';
wizardImage.src = 'images/wizard.png';
berserkerImage.src = 'images/berserker.png';
priestImage.src = 'images/priest.png';

let classImages = [knightImage, wizardImage, berserkerImage, priestImage];
let canvases = [];
let playerSlots = [];

let readyButton;

$(document).ready(function() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $('#ctn_lost_warning').hide();

    canvases = [
        document.getElementById('player1').getContext('2d'),
        document.getElementById('player2').getContext('2d'),
        document.getElementById('player3').getContext('2d'),
        document.getElementById('player4').getContext('2d'),
    ];

    playerSlots = [
        {name: $('#name1'), sprite: canvases[0], pop: $('#pop1')},
        {name: $('#name2'), sprite: canvases[1], pop: $('#pop2')},
        {name: $('#name3'), sprite: canvases[2], pop: $('#pop3')},
        {name: $('#name4'), sprite: canvases[3], pop: $('#pop4')}
    ];

    roomCode = getUrlParameter('code');
    connect(roomCode);
    $('#room_code').text('Room: ' + roomCode);

    $('[data-toggle="popover"]').popover();

    $.post('userInfo/myInfo', function (result) {
        myInfo = result;
    });

    readyButton = $('#ready_bt');
    readyButton.click(function () {
        sendMessage('/app/room.ready/' + roomCode, ''); // toggle
    });
});



/*
 * Websocket connectivity
 */


let stompClient = null;

function connect(code) {
    let socket = new SockJS('/room');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/room/' + code, onMessageReceived);
        stompClient.subscribe('/user/topic/room/' + code, onMessageReceived);
        stompClient.send('/app/room.enter/' + code, {}, code);
    }, onError);
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
}

function onError(error) {
    if (typeof(error) === "string" && error.indexOf('Whoops!') !== -1) {
        $('#ctn_lost_warning').show();
        return;  // lost connection
    }
    // disconnect();
}

function onMessageReceived(message) {
    let messageBody = JSON.parse(message.body);
    if (messageBody.type === 'Info') {
        messageBody.players.forEach(function (info) {
            addPlayer(info);
            displayPlayer(info);
        });
    }
    else if (messageBody.type === 'Chat') {

    }
    else if (messageBody.type === 'Enter') {
        addPlayer(messageBody.player);
        displayPlayer(messageBody.player);
    }
    else if (messageBody.type === 'Leave') {
        removePlayer(messageBody.name);
    }
    else if (messageBody.type === 'Ready') {
        setPlayerReady(messageBody.name, messageBody.ready);
    }
    else if (messageBody.type === 'AllReady') {
        startCountdown(5);
    }
    else if (messageBody.type === 'Start') {
        disconnect();
        window.location.replace('/controller?code=' + roomCode);
    }
}

function sendMessage(destination, data) {
    stompClient.send(destination, {}, data);
}

function addPlayer(player) {
    playerMap[player.name] = player;
    if (player.name === myInfo.name) myPosition = player.position;
}

function displayPlayer(player) {
    let slot = playerSlots[player.position];
    slot.name.text(player.name);
    slot.sprite.drawImage(classImages[player.classId], 0, 0);
    setPlayerReady(player.name, player.ready);
}

function removePlayer(name) {
    let player = playerMap[name];
    let slot = playerSlots[player.position];
    slot.name.text('Player ' + (player.position + 1));
    slot.pop.popover('hide');
    let canvas = slot.sprite;
    canvas.clearRect(0, 0, canvas.canvas.width, canvas.canvas.height);
    playerMap[name] = undefined;
}

function setPlayerReady(name, isReady) {
    let position = playerMap[name].position;
    let pop = playerSlots[position].pop;
    isReady ? pop.popover('show') : pop.popover('hide');
    playerMap[name].ready = isReady;
    if (!isReady) stopCountdown();
}

let cd = undefined;
let waitTime;
function startCountdown(time) {
    waitTime = time;
    cd = setInterval(countdown, 1000);
}

function stopCountdown() {
    if (typeof (cd) !== 'undefined') {
        clearInterval(cd);
        cd = undefined;
    }
    readyButton.text('Ready!');
}

function countdown() {
    readyButton.text('Ready(' + waitTime + ')');
    if (waitTime === 0) {
        stopCountdown();
        sendMessage('/app/room.start/' + roomCode, '');
    }
    waitTime--;
}

function getUrlParameter(sParam) {
    let sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
}