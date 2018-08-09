"use strict";

let game = new Phaser.Game(window.innerWidth, window.innerHeight, Phaser.AUTO, 'game', this);
let deck = new Array(52);
let code;
let gameStart = false;

function init() {
    game.scale.scaleMode = Phaser.ScaleManager.SHOW_ALL;
    game.scale.pageAlignHorizontally = true;
    game.scale.pageAlignVertically = true;

    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
}

function preload() {
    this.load.pack('game', 'assets/Pack.json');

    // load deck information
    $.post('deck/cards', function (result) {
        result.forEach(function (card) {
            card['suit'] = Math.floor(card.indecks / 13);
            card['rank'] = card.indecks - card.suit * 13 + 1;
            deck[card.indecks] = card;
        });
        for (let i = 0; i < 52; i++) {
            if (typeof(deck[i]) === 'undefined') {
                deck[i] = starter[i];
                deck[i]['suit'] = Math.floor(i / 13);
                deck[i]['rank'] = i - deck[i].suit * 13 + 1;
            }
        }
    });

    code = getUrlParameter('code');
    connect(code);
}

function create() {

}

function update() {

}

function setUpUI() {
    let card = new Card(this.game, undefined, 'card', false, false, Phaser.Physics.ARCADE, 0, 0, deck[0]);
    card = new Card(this.game, undefined, 'card', false, false, Phaser.Physics.ARCADE, 80, 80, deck[4]);

    let right = new RightPanel(this.game, undefined, 'rightPanel', false, false, Phaser.Physics.ARCADE);
}


let stompClient = null;

function connect(code) {
    let socket = new SockJS('/game');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/game/' + code, onMessageReceived);
        stompClient.send('/app/game.enter/' + code, {}, code);
    }, onError);
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
}

function onError(error) {
    if (typeof(error) === "string" && error.indexOf('Whoops!') !== -1) {
        // lost connection
    }
    // disconnect();
}

function onMessageReceived(message) {
    console.log(message);
    let messageBody = JSON.parse(message.body);
    switch (messageBody.type) {
        case 'EnterSuccessful':
            console.log('success');
            break;
        case 'GameStart':
            console.log('start');
            break;
        default:
            break;
    }
}

function sendMessage(destination, data) {
    stompClient.send(destination, {}, data);
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