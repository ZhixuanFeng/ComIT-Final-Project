"use strict";

/*
 * Phaser setup
 */

let game = new Phaser.Game(/*window.innerWidth, window.innerHeight*/800, 600, Phaser.AUTO, 'game', this);
let myInfo;
let deck = new Array(52);
let hand;
let players = [];
let enemies = [];
let code;
let gameStart = false;
let isMyTurn = false;
let gamePhase;
let currentPlayer;

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
}

function create() {
    console.log('create state');
    connect(code);

    game.stage.backgroundColor = '#F5DEB3';
    let right = new RightPanel(this.game, undefined, 'rightPanel', false, false, Phaser.Physics.ARCADE);
}

function update() {

}

function setHand(pile) {
    hand = [];
    for (let i = 0; i < pile.length; i++) {
        hand.push(new Card(game, undefined, 'card', false, false, Phaser.Physics.ARCADE, 50 + i * 50, 200, deck[pile[i]]));
    }
}

function spawnPlayers(playerInfos) {
    let xPos = 50;
    playerInfos.forEach(function (playerInfo) {
        players.push(new Player(game, undefined, 'player', false, false, Phaser.Physics.ARCADE, xPos, 120, playerInfo));
        xPos += 60;
    });
}

function spawnEnemy(enemyInfo) {
    enemies.push(new Enemy(game, undefined, 'enemy', false, false, Phaser.Physics.ARCADE, 50, 35, enemyInfo));
}

function setCurrentPlayer(currentPlayerName) {
    currentPlayer = currentPlayerName;
    if (currentPlayerName === myInfo.name) {
        isMyTurn = true;
    }
}

function playCard() {
    if (!isMyTurn || selectedCards.length === 0) return;

    let playingCards = [];
    selectedCards.forEach(function (card) {
        playingCards.push(card.indecks);
    });
    switch (targetingMode) {
        case targetingModeEnum.none:
            // TODO: remind user to pick target
            return;
            break;
        case targetingModeEnum.player:
            sendMessage('/app/game.playCard/' + code, JSON.stringify({
                cards: playingCards,
                mode: targetingMode,
                target: selectedPlayer.info.name
            }));
            break;
        case targetingModeEnum.enemy:
            sendMessage('/app/game.playCard/' + code, JSON.stringify({
                cards: playingCards,
                mode: targetingMode,
                target: selectedEnemy.info.id
            }));
            break;
        case targetingModeEnum.allPlayers:
            sendMessage('/app/game.playCard/' + code, JSON.stringify({cards: playingCards, mode: targetingMode}));
            break;
        case targetingModeEnum.allEnemies:
            sendMessage('/app/game.playCard/' + code, JSON.stringify({cards: playingCards, mode: targetingMode}));
            break;
        default:
            return;
    }

    cancelSelection();
}

function cancelSelection() {
    if (!isMyTurn) return;

    clearAllCardSelection();
    clearAllPlayerSelection();
    clearAllEnemySelection();
    selectedPlayer = undefined;
    selectedEnemy = undefined;
    targetingMode = targetingModeEnum.none;
    selectedCards = [];
    clearEffects();
}

function discardCard() {
    if (!isMyTurn || selectedCards.length === 0) return;


    cancelSelection();
}

function endTurn() {
    if (!isMyTurn) return;
    cancelSelection();

    sendMessage('/app/game.endTurn/' + code, {});
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
        case 'RoundStart':
            gamePhase = 'RoundStart';
            break;
        case 'AttackPhase':
            gamePhase = 'AttackPhase';
            break;
        case 'DefendPhase':
            gamePhase = 'DefendPhase';
            break;
        case 'PlayerStartsTurn':
            setCurrentPlayer(messageBody.name);
            break;
        case 'PlayerInfo':
            spawnPlayers(messageBody.players);
            break;
        case 'Hand':
            setHand(messageBody.pile);
            break;
        case 'NewEnemy':
            spawnEnemy(messageBody.enemy);
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