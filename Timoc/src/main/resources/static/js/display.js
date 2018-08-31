"use strict";

/*
 * Phaser setup
 */

let game = new Phaser.Game(/*window.innerWidth, window.innerHeight*/640, 480, Phaser.AUTO, 'game', this, false, false);
let code;
let background;
let gameInfo;
let playerMap = {};
let enemyMap = {};
let hand = [];
let currentPlayerId;
let messageBuffer = [];
let isProcessing = false;

function init() {
    game.scale.scaleMode = Phaser.ScaleManager.SHOW_ALL;
    game.scale.pageAlignHorizontally = true;
    game.scale.pageAlignVertically = true;
    game.stage.disableVisibilityChange = true;
}

function preload() {
    this.load.pack('display', '/assets/Display.json');

    code = getUrlParameter('code');
}

function create() {
    connect(code);

    let background_group = this.add.group();
    background = new Background(this.game, 0, 0, undefined, undefined);
    background_group.add(background);
}

function update() {

}

function setDisplayState(states) {
    for (let i = 0; i < 5; i++) {
        if (hand[i]) {
            let bit = Math.pow(2, 7-i);
            (states & bit) === bit ? hand[i].select() : hand[i].deselect();
        }
    }

    hand.forEach(function (card) {
        card.border.visible = false;
    });

    let cursorPosition = states >> 8;
    if (cursorPosition < 5) {
        hand[cursorPosition].border.visible = true;
    }
    for (let player in playerMap) {
        playerMap[player].sprite.border.visible = playerMap[player].sprite.posNum === cursorPosition - 6;
    }
    for (let enemy in enemyMap) {
        enemyMap[enemy].sprite.border.visible = enemyMap[enemy].sprite.posNum === cursorPosition - 10;
    }
}

function spawnPlayers(players) {
    for (let i = 0; i < players.length; i++) {
        let playerSprite = new Player(game, undefined, 'player', false, false, Phaser.Physics.ARCADE, i, players[i]);
        let playerStat = new PlayerStats(game, undefined, 'playerStats', false, false, Phaser.Physics.ARCADE, i, players[i]);
        playerMap[players[i].id] = {info: players[i], sprite: playerSprite, stat: playerStat, deck: undefined};
    }
}

function spawnEnemies(enemies) {
    for (let i = 0; i < enemies.length; i++) {
        let enemySprite = new Enemy(game, undefined, 'enemy', false, false, Phaser.Physics.ARCADE, enemies[i].position, enemies[i]);
        enemyMap[enemies[i].id] = {info: enemies[i], sprite: enemySprite};
    }
}

function newTurn(id) {
    currentPlayerId = id;
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
    addCardsToUI(cards);
}


function animateCardUse() {
    let tween;
    selectedCards.forEach(function (card) {
        card.border.visible = false;
        tween = game.add.tween(card).to( { y: card.y-50 }, 300, Phaser.Easing.Exponential.Out, true);
        card.tween = tween;
    });
    if (tween) tween.onComplete.add(removeUsedCards);
}

function removeUsedCards() {
    for (let i = 0; i < hand.length; i++) {
        if (hand[i].isSelected) {
            hand[i].kill();
            hand[i].destroy(true, false);
            hand[i] = undefined;
        }
    }
    clearCardSelection();
    moveCardsToLeft();
}

function moveCardsToLeft() {
    let newHand = [];
    let count = 0;
    let tween;
    hand.forEach(function (card) {
        if (card) {
            newHand[count] = card;
            tween = game.add.tween(card).to( { x: cardXPositions[count] }, 500, Phaser.Easing.Exponential.Out, true);
            card.tween = tween;
            card.posIndex = count;
            count++;
        }
    });
    if (tween) tween.onComplete.add(onFinishMovingLeft);
    hand = newHand;
    if (hand.length === 0) onFinishMovingLeft();

    function onFinishMovingLeft() {
        processNextMessage();
    }
}

function addCardsToUI(cards) {
    let card;
    for (let i = 0; i < cards.length; i++) {
        card = new Card(game, undefined, 'card', false, false, Phaser.Physics.ARCADE, hand.length, playerMap[currentPlayerId].deck[cards[i]]);
        hand.push(card);
        card.y -= 50;
        card.tween = game.add.tween(card).to( { y: card.y+50 }, 500, Phaser.Easing.Exponential.Out, true);
    }
    if (card.tween) card.tween.onComplete.add(onComplete);

    function onComplete() {
        processNextMessage();
    }
}



/*
 * Websocket connectivity
 */

let stompClient = null;

function connect(code) {
    let socket = new SockJS('/display');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/display/' + code, onMessageReceived);
        stompClient.send('/app/display.enter/' + code, {}, code);
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
    if (messageBody.constructor === Array) {
        messageBuffer.push.apply(messageBuffer, messageBody);
    }
    else {
        messageBuffer.push(messageBody);
    }
    processMessage()
}

function processMessage() {
    if (messageBuffer.length === 0 || isProcessing) return;

    isProcessing = true;
    let message = messageBuffer.shift();
    let id;
    switch (message.type) {
        case messageCode.GameInfo:
            gameInfo = message.game;
            processNextMessage();
            break;
        case messageCode.PlayerInfo:
            if (typeof(message.players) !== 'undefined')
                spawnPlayers(message.players);
            else
                spawnPlayers([message.player]);
            processNextMessage();
            break;
        case messageCode.EnemyInfo:
            if (typeof(message.enemies) !== 'undefined')
                spawnEnemies(message.enemies);
            else
                spawnEnemies([message.enemy]);
            processNextMessage();
            break;
        case messageCode.DState:
            setDisplayState(message.states);
            processNextMessage();
            break;
        case messageCode.PlayerUpdate:
            id = message.player.id;
            playerMap[id].info = message.player;
            playerMap[id].stat.updateStats(message.player);
            processNextMessage();
            break;
        case messageCode.EnemyUpdate:
            id = message.enemy.id;
            enemyMap[id].info = message.enemy;
            enemyMap[id].sprite.updateEnemy(message.enemy);
            processNextMessage();
            break;
        case messageCode.PlayerDeck:
            let deck = [];
            for (let i = 0; i < 52; i++) {
                deck[i] = starter[i];
            }
            message.cards.forEach(function (card) {
                deck[card.indecks] = card;
            });
            playerMap[message.id].deck = deck;
            processNextMessage();
            break;
        case messageCode.PlayerUpdateAll:
            message.players.forEach(function (player) {
                let id = player.id;
                playerMap[id].info = player;
                playerMap[id].stat.updateStats(player);
            });
            processNextMessage();
            break;
        case messageCode.EnemyUpdateAll:
            message.enemies.forEach(function (enemy) {
                let id = enemy.id;
                enemyMap[id].info = enemy;
                enemyMap[id].sprite.updateEnemy(enemy);
            });
            processNextMessage();
            break;
        case messageCode.PlayerHpChange:
            playerMap[message.id].info.hp += message.value;
            playerMap[message.id].stat.updateHp(playerMap[message.id].info.hp);
            playerMap[message.id].sprite.addHpChangeNumber(message.value);
            processNextMessage();
            break;
        case messageCode.PlayerManaChange:
            playerMap[message.id].info.mana += message.value;
            playerMap[message.id].stat.updateMana(playerMap[message.id].info.mana);
            playerMap[message.id].sprite.addManaChangeNumber(message.value);
            processNextMessage();
            break;
        case messageCode.PlayerBlockChange:
            playerMap[message.id].info.block += message.value;
            playerMap[message.id].stat.updateBlock(playerMap[message.id].info.block);
            playerMap[message.id].sprite.addBlockChangeNumber(message.value);
            processNextMessage();
            break;
        case messageCode.PlayerHateChange:
            playerMap[message.id].info.hate += message.value;
            playerMap[message.id].stat.updateHate(playerMap[message.id].info.hate);
            playerMap[message.id].sprite.addHateChangeNumber(message.value);
            processNextMessage();
            break;
        case messageCode.EnemyHpChange:
            enemyMap[message.id].info.hp += message.value;
            enemyMap[message.id].sprite.updateHp(enemyMap[message.id].info.hp);
            enemyMap[message.id].sprite.showHpChangeNumber(message.value);
            processNextMessage();
            break;
        case messageCode.PlayerStartsTurn:
            newTurn(message.id);
            processNextMessage();
            break;
        case messageCode.Hand:
            setHand(message.cards);
            break;
        case messageCode.PlayerDrawCard:
            addCardsToUI(message.cards);
            break;
        case messageCode.RemoveUsedCard:
            animateCardUse();
            break;
        case messageCode.RemoveEnemy:
            enemyMap[message.id].sprite.kill();
            enemyMap[message.id].sprite.destroy(true, false);
            delete enemyMap[message.id].sprite;
            delete enemyMap[message.id].info;
            delete enemyMap[message.id];
            processNextMessage();
            break;
        case messageCode.NotEnoughMana:
            playerMap[currentPlayerId].sprite.animateInvalidAction();
            break;
        case messageCode.NoMoreReplace:
            playerMap[currentPlayerId].sprite.animateInvalidAction();
            break;
        default:
            processNextMessage();
            break;
    }
}

function processNextMessage() {
    isProcessing = false;
    processMessage();
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
            return sParameterName[1] === undefined ? true : sParameterName[1].toUpperCase();
        }
    }
}

let messageCode = {
    Empty: 0,
    Error: 1,
    EnterSuccessful: 2,
    GameStart: 3,
    RoundStart: 4,
    AttackPhase: 5,
    DefendPhase: 6,
    GameInfo: 7,
    PlayerInfo: 8,
    EnemyInfo: 9,
    PlayerDeck: 10,
    Hand: 11,
    NewEnemy: 12,
    RemoveEnemy: 13,
    EnemyDrawsCard: 14,
    EnemyPlaysCard: 15,
    EnemyUpdate: 16,
    EnemyUpdateAll: 17,
    PlayerStartsTurn: 18,
    PlayerEndsTurn: 19,
    PlayerUpdate: 20,
    PlayerUpdateAll: 21,
    PlayerRevive: 22,
    PlayerDrawCard: 23,
    PlayCardSuccessful: 24,
    RemoveUsedCard: 25,
    PlayerHpChange: 26,
    PlayerManaChange: 27,
    PlayerBlockChange: 28,
    PlayerHateChange: 29,
    EnemyHpChange: 30,
    DState: 31,
    NotEnoughMana: 32,
    NoMoreReplace: 33
};