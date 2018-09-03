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
let currentPlayerObject;
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
    let tween;
    for (let i = 0; i < players.length; i++) {
        let playerSprite = new Player(game, undefined, 'player', false, false, Phaser.Physics.ARCADE, i, players[i]);
        let playerStat = new PlayerStats(game, undefined, 'playerStats', false, false, Phaser.Physics.ARCADE, i, players[i]);
        playerMap[players[i].id] = {info: players[i], sprite: playerSprite, stat: playerStat, deck: undefined};

        let xPos = playerSprite.x;
        playerSprite.x = 0 - 32;
        playerStat.hide();
        game.time.events.add(1200, playerStat.show, playerStat);
        tween = game.add.tween(playerSprite).to( { x: xPos }, 1000, Phaser.Easing.Linear.None, true);
    }
    if (tween) tween.onComplete.add(processNextMessage);
}

function spawnEnemies(enemies) {
    let tween;
    for (let i = 0; i < enemies.length; i++) {
        let enemySprite = new Enemy(game, undefined, 'enemy', false, false, Phaser.Physics.ARCADE, enemies[i].position, enemies[i]);
        enemyMap[enemies[i].id] = {info: enemies[i], sprite: enemySprite};

        let xPos = enemySprite.x;
        enemySprite.x = game.width + 32;
        enemySprite.doShowHpBar(false);
        game.time.events.add(1200, enemySprite.doShowHpBar, enemySprite);
        tween = game.add.tween(enemySprite).to( { x: xPos }, 1000, Phaser.Easing.Linear.None, true);
    }
    if (tween) tween.onComplete.add(processNextMessage);
}

function newTurn(id) {
    currentPlayerObject = playerMap[id];
    currentPlayerObject.sprite.standOut();
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


function animateCardRemoval() {
    let tween;
    hand.forEach(function (card) {
        card.border.visible = false;
        if (card.toBeRemoved) {
            tween = game.add.tween(card).to({y: card.y - 50}, 300, Phaser.Easing.Exponential.Out, true);
        }
    });
    if (tween) tween.onComplete.add(removeUsedCards);
}

function removeUsedCards() {
    for (let i = 0; i < hand.length; i++) {
        if (hand[i].toBeRemoved) {
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
    let card, tween;
    for (let i = 0; i < cards.length; i++) {
        card = new Card(game, undefined, 'card', false, false, Phaser.Physics.ARCADE, hand.length, currentPlayerObject.deck[cards[i]]);
        hand.push(card);
        card.y -= 50;
        tween = game.add.tween(card).to( { y: card.y+50 }, 500, Phaser.Easing.Exponential.Out, true);
    }
    if (card && tween) tween.onComplete.add(onComplete);

    function onComplete() {
        processNextMessage();
    }
}

function gameOver(ending) {
    let endingScreen = new EndingScreen(game, undefined, 'endingScreen', false, false, Phaser.Physics.ARCADE, ending);
    game.time.events.add(1500, endingScreen.fadeIn, endingScreen);
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
    let id, cards;
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
            break;
        case messageCode.EnemyInfo:
            if (typeof(message.enemies) !== 'undefined')
                spawnEnemies(message.enemies);
            else
                spawnEnemies([message.enemy]);
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
            break;
        case messageCode.PlayerEndsTurn:
            currentPlayerObject.sprite.standBack();
            break;
        case messageCode.Hand:
            setHand(message.cards);
            break;
        case messageCode.PlayerDrawCard:
            addCardsToUI(message.cards);
            break;
        case messageCode.PlayerPlaysCard_Player:
            cards = [];
            message.cards.forEach(function (indecks) {
                cards.push(playerMap[message.id].deck[indecks]);
            });
            playerMap[message.targetId].sprite.animateCardUseOnPlayer(cards);
            break;
        case messageCode.PlayerPlaysCard_Enemy:
            cards = [];
            message.cards.forEach(function (indecks) {
                cards.push(playerMap[message.id].deck[indecks]);
            });
            playerMap[message.id].sprite.animateCardUseOnEnemy(cards, enemyMap[message.targetId].sprite);
            break;
        case messageCode.PlayerDies:
            playerMap[message.id].sprite.dropDead();
            break;
        case messageCode.PlayerRevive:
            playerMap[message.id].sprite.revive();
            break;
        case messageCode.RemovePlayedCards:
            selectedCards.forEach(function (card) {
                card.toBeRemoved = true;
            });
            animateCardRemoval();
            break;
        case messageCode.RemoveCardAtPosition:
            hand[message.value].toBeRemoved = true;
            animateCardRemoval();
            break;
        case messageCode.EnemyStartsTurn:
            enemyMap[message.id].sprite.standOut();
            break;
        case messageCode.EnemyEndsTurn:
            enemyMap[message.id].sprite.standBack();
            break;
        case messageCode.EnemyPlaysCard_Player:
            enemyMap[message.id].sprite.animateCardUseOnPlayer(message.card, playerMap[message.targetId].sprite);
            break;
        case messageCode.EnemyDies:
            enemyMap[message.id].sprite.dropDead();
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
            currentPlayerObject.sprite.animateInvalidAction();
            break;
        case messageCode.NoMoreReplace:
            currentPlayerObject.sprite.animateInvalidAction();
            break;
        case messageCode.PlayerDisconnected:
            playerMap[message.id].sprite.sprite.tint = 0x606060;
            processNextMessage();
            break;
        case messageCode.PlayerReconnected:
            playerMap[message.id].sprite.sprite.tint = 0xffffff;
            processNextMessage();
            break;
        case messageCode.DisconnectDisplay:
            disconnect();
            processNextMessage();
            break;
        case messageCode.GameOverVictory:
            gameOver('good');
            break;
        case messageCode.GameOverDefeat:
            gameOver('bad');
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
    EnemyDies: 12,
    RemoveEnemy: 13,
    EnemyDrawsCard: 14,
    EnemyStartsTurn: 15,
    EnemyEndsTurn: 16,
    EnemyUpdateAll: 17,
    PlayerStartsTurn: 18,
    PlayerEndsTurn: 19,
    PlayerUpdate: 20,
    PlayerUpdateAll: 21,
    PlayerRevive: 22,
    PlayerDrawCard: 23,
    PlayerDies: 24,
    RemovePlayedCards: 25,
    PlayerHpChange: 26,
    PlayerManaChange: 27,
    PlayerBlockChange: 28,
    PlayerHateChange: 29,
    EnemyHpChange: 30,
    DState: 31,
    NotEnoughMana: 32,
    NoMoreReplace: 33,
    PlayerPlaysCard_Player: 34,
    PlayerPlaysCard_Enemy: 35,
    EnemyPlaysCard_Player: 36,
    EnemyPlaysCard_Enemy: 37,
    PlayerDisconnected: 38,
    PlayerReconnected: 39,
    DisconnectDisplay: 40,
    GameOverVictory: 41,
    GameOverDefeat: 42,
    RemoveCardAtPosition: 44
};