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
let emptyEnemyPosNum = [0, 1, 2, 3];
let hand = [];
let currentPlayerId;
let isAnimating = false;
let drawCardBuffer = [];

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
        let bit = Math.pow(2, 7-i);
        (states & bit) === bit ? hand[i].select() : hand[i].deselect();
    }

    hand.forEach(function (card) {
        card.border.visible = false;
    });

    let cursorPosition = states >> 8;
    if (cursorPosition < 5) {
        hand[cursorPosition].border.visible = true;
    }
    else if (cursorPosition > 5 && cursorPosition < 14) {
        for (let player in playerMap) {
            playerMap[player].sprite.border.visible = playerMap[player].sprite.posNum === cursorPosition - 6;
        }
        for (let enemy in enemyMap) {
            enemyMap[enemy].sprite.border.visible = enemyMap[enemy].sprite.posNum === cursorPosition - 10;
        }
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
        let enemySprite = new Enemy(game, undefined, 'enemy', false, false, Phaser.Physics.ARCADE, emptyEnemyPosNum.shift(), enemies[i]);
        enemyMap[enemies[i].id] = {info: enemies[i], sprite: enemySprite};
    }
}

function newTurn(id) {
    currentPlayerId = id;
    drawCardBuffer = [];
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
        card.graphics.visible = false;
        isAnimating = true;
        tween = game.add.tween(card).to( { y: card.y-50 }, 500, Phaser.Easing.Exponential.Out, true);
        card.tween = tween;
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
            card.tween = tween;
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
    let card;
    for (let i = 0; i < drawCardBuffer.length; i++) {
        card = new Card(game, undefined, 'card', false, false, Phaser.Physics.ARCADE, hand.length, playerMap[currentPlayerId].deck[drawCardBuffer[i]]);
        hand.push(card);
        card.y -= 50;
        isAnimating = true;
        card.tween = game.add.tween(card).to( { y: card.y+50 }, 500, Phaser.Easing.Exponential.Out, true);
    }
    if (card.tween) card.tween.onComplete.add(onComplete);
    drawCardBuffer = [];

    function onComplete() {
        isAnimating = false;
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
        messageBody.forEach(processMessage);
    }
    else {
        processMessage(messageBody);
    }
}

function processMessage(message) {
    let id;
    switch (message.type) {
        case messageCode.GameInfo:
            gameInfo = message.game;
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
            break;
        case messageCode.PlayerUpdate:
            id = message.player.id;
            playerMap[id].info = message.player;
            playerMap[id].stat.updateStats(message.player);
            break;
        case messageCode.EnemyUpdate:
            id = message.enemy.id;
            enemyMap[id].info = message.enemy;
            enemyMap[id].sprite.updateEnemy(message.enemy);
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
            break;
        case messageCode.PlayerUpdateAll:
            message.players.forEach(function (player) {
                let id = player.id;
                playerMap[id].info = player;
                playerMap[id].stat.updateStats(player);
            });
            break;
        case messageCode.EnemyUpdateAll:
            message.enemies.forEach(function (enemy) {
                let id = enemy.id;
                enemyMap[id].info = enemy;
                enemyMap[id].sprite.updateEnemy(enemy);
            });
            break;
        case messageCode.PlayerHpChange:
            playerMap[message.id].info.hp += message.value;
            playerMap[message.id].stat.updateHp(playerMap[message.id].info.hp);
            playerMap[message.id].sprite.showHpChangeNumber(message.value);
            break;
        case messageCode.PlayerManaChange:
            playerMap[message.id].info.mana += message.value;
            playerMap[message.id].stat.updateMana(playerMap[message.id].info.mana);
            break;
        case messageCode.PlayerBlockChange:
            playerMap[message.id].info.block += message.value;
            playerMap[message.id].stat.updateBlock(playerMap[message.id].info.block);
            break;
        case messageCode.PlayerHateChange:
            playerMap[message.id].info.hate += message.value;
            playerMap[message.id].stat.updateHate(playerMap[message.id].info.hate);
            break;
        case messageCode.EnemyHpChange:
            enemyMap[message.id].info.hp += message.value;
            enemyMap[message.id].sprite.updateHp(enemyMap[message.id].info.hp);
            enemyMap[message.id].sprite.showHpChangeNumber(message.value);
            break;
        case messageCode.PlayerStartsTurn:
            newTurn(message.id);
            break;
        case messageCode.Hand:
            setHand(message.pile);
            break;
        case messageCode.PlayerDrawCard:
            drawCardBuffer.push.apply(drawCardBuffer, message.cards);
            if (!isAnimating) addNewDrawnCardsToUI();
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
    DiscardCardSuccessful: 25,
    PlayerHpChange: 26,
    PlayerManaChange: 27,
    PlayerBlockChange: 28,
    PlayerHateChange: 29,
    EnemyHpChange: 30,
    DState: 31
};