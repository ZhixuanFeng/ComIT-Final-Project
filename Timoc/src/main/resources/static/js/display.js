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


function spawnPlayers(players) {
    for (let i = 0; i < players.length; i++) {
        let playerSprite = new Player(game, undefined, 'player', false, false, Phaser.Physics.ARCADE, i, players[i]);
        let playerStat = new PlayerStats(game, undefined, 'playerStats', false, false, Phaser.Physics.ARCADE, i, players[i]);
        playerMap[players[i].name] = {info: players[i], sprite: playerSprite, stat: playerStat};
    }
}

function spawnEnemies(enemies) {
    for (let i = 0; i < enemies.length; i++) {
        let enemySprite = new Enemy(game, undefined, 'enemy', false, false, Phaser.Physics.ARCADE, emptyEnemyPosNum.shift(), enemies[i]);
        enemyMap[enemies[i].id] = {info: enemies[i], sprite: enemySprite};
    }
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
    let name, id;
    switch (message.type) {
        case 'GameInfo':
            gameInfo = message.game;
            break;
        case 'PlayerInfo':
            if (typeof(message.players) !== 'undefined')
                spawnPlayers(message.players);
            else
                spawnPlayers([message.player]);
            break;
        case 'EnemyInfo':
            if (typeof(message.enemies) !== 'undefined')
                spawnEnemies(message.enemies);
            else
                spawnEnemies([message.enemy]);
            break;
        case undefined:
            if (message >= 0 && message <= 9) {

            }
            break;
        case 'PlayerUpdate':
            name = message.player.name;
            playerMap[name].info = message.player;
            playerMap[name].stat.updateStats(message.player);
            break;
        case 'EnemyUpdate':
            id = message.enemy.id;
            enemyMap[id].info = message.enemy;
            enemyMap[id].sprite.updateEnemy(message.enemy);
            break;
        case 'PlayerDeck':
            let deck = [];
            for (let i = 0; i < 52; i++) {
                deck[i] = starter[i];
            }
            message.cards.forEach(function (card) {
                deck[card.indecks] = card;
            });
            playerMap[message.name].deck = deck;
            let card = new Card(game, undefined, 'card', false, false, Phaser.Physics.ARCADE, 0, deck[0]);
            break;
        case 'PlayerUpdateAll':
            message.players.forEach(function (player) {
                let name = player.name;
                playerMap[name].info = player;
                playerMap[name].stat.updateStats(player);
            });
            break;
        case 'EnemyUpdateAll':
            message.enemies.forEach(function (enemy) {
                let id = enemy.id;
                enemyMap[id].info = enemy;
                enemyMap[id].sprite.updateEnemy(enemy);
            });
            break;
        case 'PlayerHpChange':
            playerMap[message.name].info.hp += message.value;
            playerMap[message.name].stat.updateHp(playerMap[message.name].info.hp);
            playerMap[message.name].sprite.showHpChangeNumber(message.value);
            break;
        case 'PlayerManaChange':
            playerMap[message.name].info.mana += message.value;
            playerMap[message.name].stat.updateMana(playerMap[message.name].info.mana);
            break;
        case 'PlayerBlockChange':
            playerMap[message.name].info.block += message.value;
            playerMap[message.name].stat.updateBlock(playerMap[message.name].info.block);
            break;
        case 'PlayerHateChange':
            playerMap[message.name].info.hate += message.value;
            playerMap[message.name].stat.updateHate(playerMap[message.name].info.hate);
            break;
        case 'EnemyHpChange':
            enemyMap[message.id].info.hp += message.value;
            enemyMap[message.id].sprite.updateHp(enemyMap[message.id].info.hp);
            enemyMap[message.id].sprite.showHpChangeNumber(message.value);
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