"use strict";

/*
 * Phaser setup
 */

let game = new Phaser.Game(/*window.innerWidth, window.innerHeight*/640, 480, Phaser.AUTO, 'game', this, false, false);
let code;
let background;
let gameInfo;
let playerInfo = [];
let playerSprites = [];
let playerStats = [];
let enemyInfo = [];
let enemySprites = [];
function init() {
    game.scale.scaleMode = Phaser.ScaleManager.SHOW_ALL;
    game.scale.pageAlignHorizontally = true;
    game.scale.pageAlignVertically = true;
}

function preload() {
    this.load.pack('display', 'assets/Display.json');

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
    playerInfo = players;
    for (let i = 0; i < players.length; i++) {
        playerSprites[i] = new Player(game, undefined, 'player', false, false, Phaser.Physics.ARCADE, i, players[i]);
        playerStats[i] = new PlayerStats(game, undefined, 'playerStats', false, false, Phaser.Physics.ARCADE, i, players[i]);
    }
}

function spawnEnemies(enemies) {
    enemyInfo = enemies;
    for (let i = 0; i < enemies.length; i++) {
        enemySprites[i] = new Enemy(game, undefined, 'enemy', false, false, Phaser.Physics.ARCADE, i, enemies[i]);
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