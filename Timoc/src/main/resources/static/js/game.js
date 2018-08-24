"use strict";

/*
 * Phaser setup
 */

let game = new Phaser.Game(/*window.innerWidth, window.innerHeight*/800, 600, Phaser.AUTO, 'game', this);
let myInfo;
let myPlayer;
let deck = new Array(52);
let hand = [];
let players = [];
let enemies = [];
let code;
let gameStart = false;
let isMyTurn = false;
let gamePhase;
let currentPlayerName;
let isAnimating = false;
let drawCardBuffer = [];

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
    this.load.pack('game', 'assets/Game.json');

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
    connect(code);

    game.stage.backgroundColor = '#F5DEB3';
    let right = new RightPanel(this.game, undefined, 'rightPanel', false, false, Phaser.Physics.ARCADE);
}

function update() {

}

function clearHand() {
    hand.forEach(function (card) {
        card.kill();
        card.destroy();
    });
    hand = [];
}

function setHand(cards) {
    clearHand();
    drawCardBuffer.push.apply(drawCardBuffer, cards);
    addNewDrawnCardsToUI();
}

function animateCardUse() {
    let tween;
    selectedCards.forEach(function (card) {
        // card.destroy(true, false);
        card.graphics.visible = false;
        isAnimating = true;
        tween = game.add.tween(card).to( { y: card.y-50 }, 500, Phaser.Easing.Exponential.Out, true);
    });
    if (tween) tween.onComplete.add(removeUsedCards);
}

function removeUsedCards() {
    isAnimating = false;
    myPlayer.updateBlock();
    for (let i = 0; i < hand.length; i++) {
        if (hand[i].isSelected) {
            hand[i] = undefined;
        }
    }
    selectedCards.forEach(function (card) {
        card.kill();
        card.destroy(true, false);
    });
    cancelSelection();
    moveCardsToLeft();
}

function moveCardsToLeft() {
    let newHand = [];
    let count = 0;
    let tween;
    hand.forEach(function (card) {
        if (card) {
            newHand[count] = card;
            isAnimating = true;
            tween = game.add.tween(card).to( { x: cardXPositions[count]*card.scale.x }, 500, Phaser.Easing.Exponential.Out, true);
            card.posIndex = count;
            card.repositionBorder();
            count++;
        }
    });
    if (tween) tween.onComplete.add(onFinishMovingLeft);
    hand = newHand;
    if (hand.length === 0) onFinishMovingLeft();

    function onFinishMovingLeft() {
        isAnimating = false;
        myPlayer.updateBlock();
        addNewDrawnCardsToUI();
    }
}

function addNewDrawnCardsToUI() {
    let tween;
    for (let i = 0; i < drawCardBuffer.length; i++) {
        let card = new Card(game, undefined, 'card', false, false, Phaser.Physics.ARCADE, hand.length, deck[drawCardBuffer[i]]);
        hand.push(card);
        card.y -= 50;
        isAnimating = true;
        tween = game.add.tween(card).to( { y: card.y+50 }, 500, Phaser.Easing.Exponential.Out, true);
    }
    if (tween) tween.onComplete.add(onComplete);
    drawCardBuffer = [];

    function onComplete() {
        isAnimating = false;
        myPlayer.updateBlock();
    }
}

function spawnPlayers(playerInfos) {
    let xPos = 50;
    playerInfos.forEach(function (playerInfo) {
        let newPlayer = new Player(game, undefined, 'player', false, false, Phaser.Physics.ARCADE, xPos, 120, playerInfo);
        players.push(newPlayer);
        xPos += 60;
        if (playerInfo.name === myInfo.name) {
            myPlayer = newPlayer;
        }
    });
}

function spawnEnemy(enemyInfo) {
    enemies.push(new Enemy(game, undefined, 'enemy', false, false, Phaser.Physics.ARCADE, enemies.length, enemyInfo));
}

function setCurrentPlayer(name) {
    currentPlayerName = name;
    if (name === myInfo.name) {
        isMyTurn = true;
    }
}

function updatePlayer(playerInfo) {
    for (let i = 0; i < players.length; i++) {
        if (players[i].info.name === playerInfo.name) {
            players[i].updatePlayerInfo(playerInfo);
        }
    }
}

function updateEnemy(enemyInfo) {
    for (let i = 0; i < enemies.length; i++) {
        if (enemies[i].info.id === enemyInfo.id) {
            enemies[i].updateEnemyInfo(enemyInfo);
        }
    }
}

function playCard() {
    if (!isMyTurn || selectedCards.length === 0 || isAnimating) return;

    let playingCards = [];
    selectedCards.forEach(function (card) {
        playingCards.push(card.info.indecks);
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
    if (!isMyTurn || selectedCards.length === 0 || isAnimating) return;

    let discardingCards = [];
    selectedCards.forEach(function (card) {
        discardingCards.push(card.info.indecks);
    });

    sendMessage('/app/game.discardCard/' + code, JSON.stringify({cards: discardingCards}));
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
        case 'PlayCardSuccessful':
            animateCardUse();
            break;
        case 'DiscardCardSuccessful':
            animateCardUse();
            break;
        case 'PlayerDrawCard':
            drawCardBuffer.push.apply(drawCardBuffer, messageBody.cards);
            if (!isAnimating) addNewDrawnCardsToUI();
            break;
        case 'PlayerUpdate':
            updatePlayer(messageBody.player);
            break;
        case 'EnemyUpdate':
            updateEnemy(messageBody.enemy);
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