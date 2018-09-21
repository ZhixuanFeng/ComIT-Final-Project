let playerPositions = [{x:64, y:32}, {x:64, y:64}, {x:64, y:96}, {x:64, y:128}];

function addPlayerHpChangeNumber(hpChange) {
    let hpChangeUINumber;
    // if (hpChange !== 0) {
        hpChangeUINumber = game.add.group();
        let xPos = 0;
        let absChange = Math.abs(hpChange);
        if (absChange >= 100) {
            hpChangeUINumber.create(xPos, 0, 'displayui', Math.floor(absChange / 100));
            xPos += 4;
        }
        if (absChange >= 10) {
            hpChangeUINumber.create(xPos, 0, 'displayui', Math.floor(absChange / 10));
            xPos += 4;
        }
        hpChangeUINumber.create(xPos, 0, 'displayui', Math.floor(absChange % 10));
    // }
    if (hpChange > 0) {
        hpChangeUINumber.children.forEach(function (number) {
            number.tint = 0x00ff00;
        });
        hpChangeUINumber['effectType'] = 'float up';
    }
    else {
        hpChangeUINumber.children.forEach(function (number) {
            number.tint = 0xff0000;
        });
        hpChangeUINumber['effectType'] = 'fly out';
    }
    hpChangeUINumber.scale.setTo(2.0);
    hpChangeUINumber.position.setTo(this.x+this.sprite.x+this.sprite.width, this.y+this.sprite.y+this.sprite.height);
    hpChangeUINumber.visible = false;
    this.effectNumbers.push(hpChangeUINumber);
    this.animateEffectNumbers();
}

function addPlayerManaChangeNumber(manaChange) {
    let manaChangeUINumber;
    if (manaChange > 0) {
        manaChangeUINumber = game.add.group();
        let xPos = 0;
        if (manaChange >= 100) {
            let digit = manaChangeUINumber.create(xPos, 0, 'displayui', Math.floor(manaChange / 100));
            digit.tint = 0x57c7ff;
            xPos += 4;
        }
        if (manaChange >= 10) {
            let digit = manaChangeUINumber.create(xPos, 0, 'displayui', Math.floor(manaChange / 10));
            digit.tint = 0x57c7ff;
            xPos += 4;
        }
        let digit = manaChangeUINumber.create(xPos, 0, 'displayui', Math.floor(manaChange % 10));
        digit.tint = 0x57c7ff;

        manaChangeUINumber.scale.setTo(2.0);
        manaChangeUINumber.position.setTo(this.x+this.sprite.x+this.sprite.width-7, this.y+this.sprite.y+this.sprite.height);
        manaChangeUINumber.visible = false;
        manaChangeUINumber['effectType'] = 'float up';
        this.effectNumbers.push(manaChangeUINumber);
        this.animateEffectNumbers();
    }
}

function addPlayerBlockChangeNumber(blockChange) {
    let blockChangeUINumber;
    if (blockChange !== 0) {
        blockChangeUINumber = game.add.group();
        let isPositive = blockChange > 0;
        let xPos = 0;
        blockChangeUINumber.create(xPos, 0, 'displayui', 'blockmini');
        xPos += 7;
        let sign = this.game.add.sprite(xPos, 0, 'displayui', isPositive ? 'plus' : 'minus', blockChangeUINumber);
        sign.tint = isPositive ? 0x57c7ff : 0xff0000;
        xPos += 4;

        let absChange = Math.abs(blockChange);
        if (absChange >= 10) {
            let digit = blockChangeUINumber.create(xPos, 0, 'displayui', Math.floor(absChange / 10));
            digit.tint = isPositive ? 0x57c7ff : 0xff0000;
            xPos += 4;
        }
        let digit = blockChangeUINumber.create(xPos, 0, 'displayui', Math.floor(absChange % 10));
        digit.tint = isPositive ? 0x57c7ff : 0xff0000;
    }
    blockChangeUINumber.scale.setTo(2.0);
    blockChangeUINumber.position.setTo(this.x+this.sprite.x+this.sprite.width-7, this.y+this.sprite.y+this.sprite.height);
    blockChangeUINumber.visible = false;
    blockChangeUINumber['effectType'] = 'float up';
    this.effectNumbers.push(blockChangeUINumber);
    this.animateEffectNumbers();
}

function addPlayerHateChangeNumber(hateChange) {
    let hateChangeUINumber;
    if (hateChange !== 0) {
        hateChangeUINumber = game.add.group();
        let isPositive = hateChange > 0;
        let xPos = 0;
        hateChangeUINumber.create(xPos, 0, 'displayui', 'hatemini');
        xPos += 7;
        let sign = this.game.add.sprite(xPos, 0, 'displayui', isPositive ? 'plus' : 'minus', hateChangeUINumber);
        sign.tint = isPositive ? 0xff0000 : 0x00ff00;
        xPos += 4;

        let absChange = Math.abs(hateChange);
        if (absChange >= 10) {
            let digit = hateChangeUINumber.create(xPos, 0, 'displayui', Math.floor(absChange / 10));
            digit.tint = isPositive ? 0xff0000 : 0x00ff00;
            xPos += 4;
        }
        let digit = hateChangeUINumber.create(xPos, 0, 'displayui', Math.floor(absChange % 10));
        digit.tint = isPositive ? 0xff0000 : 0x00ff00;
    }
    hateChangeUINumber.scale.setTo(2.0);
    hateChangeUINumber.position.setTo(this.x+this.sprite.x+this.sprite.width-7, this.y+this.sprite.y+this.sprite.height);
    hateChangeUINumber.visible = false;
    hateChangeUINumber['effectType'] = 'float up';
    this.effectNumbers.push(hateChangeUINumber);
    this.animateEffectNumbers();
}

function animatePlayerEffectNumber() {
    if (this.effectNumbers.length > 0 && !this.isAnimatingNumber) {
        let player = this;
        let number = this.effectNumbers.shift();
        number.visible = true;

        let destinationY, destinationX, tween, tween2, tween3;
        switch (number['effectType']) {
            case 'fly out':
                destinationY = number.y - 32;
                destinationX = number.x - 48;
                tween = this.game.add.tween(number).to( { y: destinationY }, 500, Phaser.Easing.Exponential.Out, true);
                tween2 = this.game.add.tween(number).to( { x: destinationX }, 1500, Phaser.Easing.Linear.None, true);

                this.isAnimatingNumber = true;
                tween.onComplete.add(function () {
                    tween3 = player.game.add.tween(number).to( { y: destinationY+32 }, 1000, Phaser.Easing.Bounce.Out, true);
                    tween3.onComplete.add(removeNumber);
                    startNextAnimation()
                });
                break;

            case 'float up':
                destinationY = number.y - 32;
                tween = this.game.add.tween(number).to( { y: destinationY }, 1000, Phaser.Easing.Linear.None, true);
                this.isAnimatingNumber = true;
                game.time.events.add(500, startNextAnimation, player);
                tween.onComplete.add(removeNumber);
                break;
        }

        function removeNumber() {
            number.kill();
            number.destroy(true, false);
        }

        function startNextAnimation() {
            player.isAnimatingNumber = false;
            player.animateEffectNumbers();
        }
    }
}

function animatePlayerCardUseOnPlayer(cards) {
    let player = this, game = this.game, delay = 0, x = this.x + 48, y = this.y + 16;
    let cardMinis = [];
    cards.forEach(function (card) {
        game.time.events.add(delay, createCardMini, player);
        delay += 300;
    });
    game.time.events.add(300 * cards.length, processNextMessage, game);
    game.time.events.add(500 * cards.length, finish, game);

    function createCardMini() {
        let cardMini = new CardMini(game, undefined, 'cardMini', false, false, Phaser.Physics.ARCADE, cards[cardMinis.length]);
        cardMini.position.setTo(x, y);
        cardMinis.push(cardMini);
        x += 34;
    }

    function finish() {
        cardMinis.forEach(function (cardMini) {
            cardMini.kill();
            cardMini.destroy(true, false);
        });
    }
}

function animatePlayerCardUseOnEnemy(cards, enemy) {
    let game = this.game;
    let player = this;
    let originX = player.x, originY = player.y;
    let delay = 0;
    let tween1 = game.add.tween(player).to( { x: originX-8 }, 100, Phaser.Easing.Linear.None, true);
    tween1.onComplete.add(function () {
        let tween2 = game.add.tween(player).to({x: originX}, 100, Phaser.Easing.Linear.None, true);
        tween2.onComplete.add(shootCards);
    });

    function shootCards() {
        cards.forEach(function (card) {
            game.time.events.add(delay, createCardMini, player);
            delay += 300;
        });
        game.time.events.add(300 * cards.length, processNextMessage, game);

        let count = 0;
        function createCardMini() {
            let cardMini = new CardMini(game, undefined, 'cardMini', false, false, Phaser.Physics.ARCADE, cards[count]);
            cardMini.position.setTo(originX, originY);
            count++;
            cardMini.spin = true;
            let tween = game.add.tween(cardMini).to({x: enemy.x, y: enemy.y}, 200, Phaser.Easing.Linear.None, true);
            tween.onComplete.add(function () {
                cardMini.kill();
                cardMini.destroy(true, false);
            });
        }
    }
}

function animateInvalidPlayerAction() {
    let game = this.game;
    let player = this;
    let originX = player.x;
    let tween1 = game.add.tween(player).to( { x: originX+8 }, 100, Phaser.Easing.Linear.None, true);
    tween1.onComplete.add(function () {
        let tween2 = game.add.tween(player).to( { x: originX-8 }, 100, Phaser.Easing.Linear.None, true);
        tween2.onComplete.add(function () {
            let tween3 = game.add.tween(player).to( { x: originX+8 }, 100, Phaser.Easing.Linear.None, true);
            tween3.onComplete.add(function () {
                let tween4 = game.add.tween(player).to( { x: originX-8 }, 100, Phaser.Easing.Linear.None, true);
                tween4.onComplete.add(function () {
                    let tween5 = game.add.tween(player).to( { x: originX }, 100, Phaser.Easing.Linear.None, true);
                    tween5.onComplete.add(processNextMessage);
                })
            })
        })
    })
}

function playerStandsOut() {
    let tween = game.add.tween(this).to( { x: playerPositions[this.posNum].x * this.scale.x+48 }, 300, Phaser.Easing.Linear.None, true);
    tween.onComplete.add(processNextMessage);
}

function playerStandsBack() {
    let tween = game.add.tween(this).to( { x: playerPositions[this.posNum].x * this.scale.x }, 300, Phaser.Easing.Linear.None, true);
    tween.onComplete.add(processNextMessage);
}

function playerDropDead() {
    let tween = game.add.tween(this.sprite).to( { angle: -90, y: this.sprite.y+24}, 500, Phaser.Easing.Exponential.Out, true);
    tween.onComplete.add(processNextMessage);
}

function playerRevive() {
    let tween = game.add.tween(this.sprite).to( { angle: 0, y: this.spriteYPos}, 1000, Phaser.Easing.Exponential.Out, true);
    tween.onComplete.add(processNextMessage);
}
// -- user code here --

/* --- start generated code --- */

// Generated by  1.5.1 (Phaser v2.6.2)


/**
 * Player.
 * @param {Phaser.Game} aGame A reference to the currently running game.
 * @param {Phaser.Group} aParent The parent Group (or other {@link DisplayObject}) that this group will be added to.
    If undefined/unspecified the Group will be added to the {@link Phaser.Game#world Game World}; if null the Group will not be added to any parent.
 * @param {string} aName A name for this group. Not used internally but useful for debugging.
 * @param {boolean} aAddToStage If true this group will be added directly to the Game.Stage instead of Game.World.
 * @param {boolean} aEnableBody If true all Sprites created with {@link #create} or {@link #createMulitple} will have a physics body created on them. Change the body type with {@link #physicsBodyType}.
 * @param {number} aPhysicsBodyType The physics body type to use when physics bodies are automatically added. See {@link #physicsBodyType} for values.
 */
function Player(aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType, posNum, playerInfo) {

	Phaser.Group.call(this, aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType);

	this.posNum = posNum;
    // this.info = playerInfo;

    this.playerClass = playerInfo.playerClass.toLowerCase();
    this.spriteYPos = 10.0;
    if (this.playerClass === 'knight') this.spriteYPos = 6.0;
	this.game.add.sprite(0.0, 21.0, 'entity', 'shadow', this);
	this.sprite = this.game.add.sprite(0.0, this.spriteYPos, 'entity', this.playerClass, this);

	let _nametext = this.game.add.text(this.sprite.width/2, 0.0, playerInfo.name, {"font":"20px Arial", "fill":"#FFFFFF"}, this);
	_nametext.anchor.setTo(0.5, 0.0);
	_nametext.scale.setTo(0.3, 0.3);

    this.border = game.add.graphics(0, 0, this);
    this.border.lineStyle(2, 0xFFFF00, 1);
    this.border.drawRoundedRect(this.sprite.x-1, this.spriteYPos-1, this.sprite.width + 2, this.sprite.height + 2, 5);
    this.border.visible = false;

    this.scale.setTo(2, 2);
    this.position.setTo(playerPositions[posNum].x * this.scale.x, playerPositions[posNum].y * this.scale.y);

    this.effectNumbers = [];
}

/** @type Phaser.Group */
let Player_proto = Object.create(Phaser.Group.prototype);
Player.prototype = Player_proto;
Player.prototype.constructor = Player;

/* --- end generated code --- */
// -- user code here --
Player.prototype.addHpChangeNumber = addPlayerHpChangeNumber;
Player.prototype.addManaChangeNumber = addPlayerManaChangeNumber;
Player.prototype.addBlockChangeNumber = addPlayerBlockChangeNumber;
Player.prototype.addHateChangeNumber = addPlayerHateChangeNumber;
Player.prototype.animateEffectNumbers = animatePlayerEffectNumber;
Player.prototype.animateCardUseOnPlayer = animatePlayerCardUseOnPlayer;
Player.prototype.animateCardUseOnEnemy = animatePlayerCardUseOnEnemy;
Player.prototype.animateInvalidAction = animateInvalidPlayerAction;
Player.prototype.standOut = playerStandsOut;
Player.prototype.standBack = playerStandsBack;
Player.prototype.dropDead = playerDropDead;
Player.prototype.revive = playerRevive;