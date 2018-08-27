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
        playerMap[players[i].name] = {playerInfo: players[i], playerSprite: playerSprite, playerStat: playerStat};
    }
}

function spawnEnemies(enemies) {
    for (let i = 0; i < enemies.length; i++) {
        let enemySprite = new Enemy(game, undefined, 'enemy', false, false, Phaser.Physics.ARCADE, i, enemies[i]);
        enemyMap[enemies[i].id] = {enemyInfo: enemies[i], enemySprite: enemySprite};
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
    switch (message.type) {
        case 'GameInfo':
            gameInfo = message.game;
            break;
        case 'PlayerInfo':
            spawnPlayers(message.players);
            break;
        case 'EnemyInfo':
            spawnEnemies(message.enemies);
            break;
        case 'PlayerUpdate':
            let name = message.player.name;
            playerMap[name].playerInfo = message.player;
            playerMap[name].playerStat.updateStats(message.player);
            break;
        case 'EnemyUpdate':
            let id = message.enemy.id;
            enemyMap[id].enemyInfo = message.enemy;
            enemyMap[id].enemySprite.updateEnemy(message.enemy);
            break;
        case 'PlayerUpdateAll':
            message.players.forEach(function (player) {
                let name = player.name;
                playerMap[name].playerInfo = player;
                playerMap[name].playerStat.updateStats(player);
            });
            break;
        case 'EnemyUpdateAll':
            message.enemies.forEach(function (enemy) {
                let id = enemy.id;
                enemyMap[id].enemyInfo = enemy;
                enemyMap[id].enemySprite.updateEnemy(enemy);
            });
            break;
        case 'PlayerHpChange':
            playerMap[message.name].playerInfo.hp += message.value;
            playerMap[message.name].playerStat.updateHp(playerMap[message.name].playerInfo.hp);
            playerMap[message.name].playerSprite.showHpChangeNumber(message.value);
            break;
        case 'PlayerManaChange':
            playerMap[message.name].playerInfo.mana += message.value;
            playerMap[message.name].playerStat.updateMana(playerMap[message.name].playerInfo.mana);
            break;
        case 'PlayerBlockChange':
            playerMap[message.name].playerInfo.block += message.value;
            playerMap[message.name].playerStat.updateBlock(playerMap[message.name].playerInfo.block);
            break;
        case 'PlayerHateChange':
            playerMap[message.name].playerInfo.hate += message.value;
            playerMap[message.name].playerStat.updateHate(playerMap[message.name].playerInfo.hate);
            break;
        case 'EnemyHpChange':
            enemyMap[message.id].enemyInfo.hp += message.value;
            enemyMap[message.id].enemySprite.updateHp(enemyMap[message.id].enemyInfo.hp);
            enemyMap[message.id].enemySprite.showHpChangeNumber(message.value);
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