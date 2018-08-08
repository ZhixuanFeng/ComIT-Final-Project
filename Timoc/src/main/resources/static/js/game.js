"use strict"
/*
let game;
let deck = new Array(52);
$(document).ready(function() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    // load information
    $.post('deck/cards', function (result) {
        result.forEach(function (card) {
            card['suit'] = Math.floor(card.indecks / 13);
            card['rank'] = card.indecks - card.suit * 13 + 1;
            deck[card.indecks] = card;
        });
    });


    function Card(cardInfo, game, x, y) {
        Phaser.Sprite.call(this, game, x, y, 'emptyCard');

        // this.id = cardInfo.id;
        // this.ownerType = cardInfo.ownerType;
        // this.quality = cardInfo.quality;
        // this.indecks = cardInfo.quality;
        // this.suit = Math.floor(this.indecks / 13);
        // this.rank = this.indecks - this.suit * 13 + 1;
        // this.attack = cardInfo.attack;
        // this.block = cardInfo.block;
        // this.heal = cardInfo.heal;
        // this.mana = cardInfo.mana;
        // this.aoe = cardInfo.aoe;
        // this.random = cardInfo.random;
        // this.revive = cardInfo.revive;
        // this.taunt = cardInfo.taunt;

        let group = game.add.group();
        group.create(80, 80, 'emptyCard');
        this.inputEnabled = true;
        this.events.onInputDown.add(function (card) {
            card.position.x -= 1;
            console.log("clicked");
        }, this);

    }
    Card.prototype = Object.create(Phaser.Sprite.prototype);
    Card.prototype.constructor = Card;

    Card.prototype.update = function () {
        this.position.x += 0.1;
    };



    let Game = {};

    Game.preload = function () {
        game.load.image('emptyCard', 'images/empty_card.png');
        game.load.image('diamond', 'images/diamond.png');
        game.load.image('club', 'images/club.png');
        game.load.image('heart', 'images/heart.png');
        game.load.image('spade', 'images/spade.png');
        game.load.image('attack', 'images/attack.png');
        game.load.image('block', 'images/block.png');
        game.load.image('heal', 'images/heal.png');
        game.load.image('mana', 'images/mana.png');
        game.load.image('aoe', 'images/aoe.png');
        game.load.image('draw', 'images/draw.png');
        game.load.image('revive', 'images/revive.png');
        game.load.image('taunt', 'images/hate.png');
    };

    Game.create = function () {
        let card = new Card(deck[0], game, 80, 80);
    };

    Game.resize = function () {

    };





    game = new Phaser.Game(window.innerWidth, window.innerHeight, Phaser.AUTO, 'game');
    game.state.add('game', Game);
    game.state.start('game');
});
*/



let game = new Phaser.Game(window.innerWidth, window.innerHeight, Phaser.AUTO, 'game', this);
let deck = new Array(52);

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
        console.log(deck);
    });
}

function create() {
    var card = new Card(this.game, undefined, 'card', false, false, Phaser.Physics.ARCADE, 0, 0, deck[0]);
    card = new Card(this.game, undefined, 'card', false, false, Phaser.Physics.ARCADE, 80, 80, deck[4]);

    var right = new RightPanel(this.game, undefined, 'rightPanel', false, false, Phaser.Physics.ARCADE);
}

function update() {

}