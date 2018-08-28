"use strict";

/*
 * Phaser setup
 */
let scale = 4;
let game = new Phaser.Game(128*scale, 64*scale, Phaser.AUTO, 'game', this, false, false);
let bgColors = ['#F5DEB3', '#1E90FF', '#FF7F50', '#32CD32', '#696969'];
let buttonCode = {'up':1, 'down':2, 'left':3, 'right':4, 'play':5, 'cancel':6, 'replace':7, 'discard':8, 'next':0};
let code;
let isGameStarted = false;
let instruction;
let btnGroup;

function init() {
    game.scale.scaleMode = Phaser.ScaleManager.SHOW_ALL;
    game.scale.pageAlignHorizontally = true;
    game.scale.pageAlignVertically = true;
    game.stage.disableVisibilityChange = true;
    this.game.renderer.renderSession.roundPixels = true;
    Phaser.Canvas.setImageRenderingCrisp(this.game.canvas);
}

function preload() {
    this.load.pack('atlas', 'assets/Controller.json');

    code = getUrlParameter('code');
}

function create() {
    connect(code);

    game.stage.backgroundColor = bgColors[Math.floor((Math.random() * bgColors.length) + 1)-1];

    instruction = this.add.text(14.0, 85.0, 'Please open a browser on another device, \ngo to timoc.me/display and enter code: ' + code, {"font":"bold 20px Arial"});
    instruction.visible = false;

    btnGroup = this.add.group();

    let _up = this.add.button(25.0*scale, 18.0*scale, 'controller', function () {onButtonPressed('up');}, this, null, 'directionalbutton', 'directionalbuttonpressed', 'directionalbutton', btnGroup);
    _up.pivot.setTo(8.0, 8.0);
    _up.scale.setTo(scale);

    let _down = this.add.button(25.0*scale, 50.0*scale, 'controller', function () {onButtonPressed('down');}, this, null, 'directionalbutton', 'directionalbuttonpressed', 'directionalbutton', btnGroup);
    _down.angle = 180.0;
    _down.pivot.setTo(8.0, 8.0);
    _down.scale.setTo(scale);

    let _left = this.add.button(9.0*scale, 34.0*scale, 'controller', function () {onButtonPressed('left');}, this, null, 'directionalbutton', 'directionalbuttonpressed', 'directionalbutton', btnGroup);
    _left.angle = 270.0;
    _left.pivot.setTo(8.0, 8.0);
    _left.scale.setTo(scale);

    let _right = this.add.button(41.0*scale, 34.0*scale, 'controller', function () {onButtonPressed('right');}, this, null, 'directionalbutton', 'directionalbuttonpressed', 'directionalbutton', btnGroup);
    _right.angle = 90.0;
    _right.pivot.setTo(8.0, 8.0);
    _right.scale.setTo(scale);

    let _playbtn = this.add.button(115.0*scale, 34.0*scale, 'controller', function () {onButtonPressed('play');}, this, null, 'playbtn', 'playbtnpressed', 'playbtn', btnGroup);
    _playbtn.pivot.setTo(8.0, 8.0);
    _playbtn.scale.setTo(scale);

    let _cancelbtn = this.add.button(99.0*scale, 50.0*scale, 'controller', function () {onButtonPressed('cancel');}, this, null, 'cancelbtn', 'cancelbtnpressed', 'cancelbtn', btnGroup);
    _cancelbtn.pivot.setTo(8.0, 8.0);
    _cancelbtn.scale.setTo(scale);

    let _replacebtn = this.add.button(99.0*scale, 18.0*scale, 'controller', function () {onButtonPressed('replace');}, this, null, 'replacebtn', 'replacebtnpressed', 'replacebtn', btnGroup);
    _replacebtn.pivot.setTo(8.0, 8.0);
    _replacebtn.scale.setTo(scale);

    let _discardbtn = this.add.button(83.0*scale, 34.0*scale, 'controller', function () {onButtonPressed('discard');}, this, null, 'discardbtn', 'discardbtnpressed', 'discardbtn', btnGroup);
    _discardbtn.pivot.setTo(8.0, 8.0);
    _discardbtn.scale.setTo(scale);

    let _nextbtn = this.add.button(62.0*scale, 18.0*scale, 'controller', function () {onButtonPressed('next');}, this, null, 'nextbtn', 'nextbtnpressed', 'nextbtn', btnGroup);
    _nextbtn.pivot.setTo(8.0, 8.0);
    _nextbtn.scale.setTo(scale);

    btnGroup.visible = false;
}

function onButtonPressed(buttonName) {
    sendMessage('/app/controller/' + code, buttonCode[buttonName]);
}


/*
 * Websocket connectivity
 */

let stompClient = null;

function connect(code) {
    let socket = new SockJS('/controller');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/controller/' + code, onMessageReceived);
        stompClient.subscribe('/user/topic/controller/' + code, onMessageReceived);
        stompClient.send('/app/controller.enter/' + code, {}, code);
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
            if (!isGameStarted) {
                instruction.visible = true;
            }
            break;
        case 'GameStart':
            isGameStarted = true;
            instruction.visible = false;
            btnGroup.visible = true;
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