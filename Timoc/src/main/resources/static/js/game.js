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

    // let group = game.add.group();
    // group.create(80, 80, 'emptyCard');
    // group.create()
    this.inputEnabled = true;
    this.events.onInputDown.add(function (card) {
        // card.position.x -= 1;
        console.log("clicked");
    }, this);

};
Card.prototype = Object.create(Phaser.Sprite.prototype);
Card.prototype.constructor = Card;

Card.prototype.update = function () {
    // this.position.x += 0.1;
};



Game = {};

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

};

Game.resize = function () {

};


let game;
let deck = new Array[52];
window.onload = function () {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e,xhr,options) {
        xhr.setRequestHeader(header, token);
    });

    // load information
    $.post('deck/cards', function (result) {
        result.forEach(function (card) {
            card[suit] = Math.floor(card.indecks / 13);
            card[rank] = card.indecks - card.suit * 13 + 1;
            deck[card.indecks] = card;
        });
    });

    game = new Phaser.Game(window.innerWidth, window.innerHeight, Phaser.AUTO, 'game');
    game.state.add('game', Game);
    game.state.start('game');
};