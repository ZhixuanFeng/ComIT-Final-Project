"use strict";

/*
 * Phaser setup
 */

let game = new Phaser.Game(/*window.innerWidth, window.innerHeight*/800, 600, Phaser.AUTO, 'game', this);
let myInfo;
let deck = new Array(52);
let hand;
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

    $.post('userInfo/myInfo', function (result) {
        myInfo = result;
    });

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
    console.log('create state');

    let right = new RightPanel(this.game, undefined, 'rightPanel', false, false, Phaser.Physics.ARCADE);
}

function update() {

}

function setHand(pile) {
    hand = [];
    for (let i = 0; i < pile.length; i++) {
        hand.push(new Card(game, undefined, 'card1', false, false, Phaser.Physics.ARCADE, 50 + i * 50, 200, deck[pile[i]]));
    }
}

/*
 * Websocket connectivity
 */

let stompClient = null;

function connect(code) {
    let socket = new SockJS('/game');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/game/' + code, onMessageReceived);
        stompClient.subscribe('/user/topic/game/' + code, onMessageReceived);
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
    let messageBody = JSON.parse(message.body);
    switch (messageBody.type) {
        case 'EnterSuccessful':
            break;
        case 'GameStart':
            break;
        case 'Hand':
            setHand(messageBody.pile);
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