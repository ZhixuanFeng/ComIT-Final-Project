let enemyPositions = [{x:240, y:42}, {x:240, y:74}, {x:240, y:106}, {x:240, y:138}];

function updateEnemyHp(hp) {
    if (hp >= 100) {
        this.hpDigit1.loadTexture('displayui', Math.floor(hp / 100));
        this.hpDigit2.loadTexture('displayui', Math.floor((hp % 100) / 10));
        this.hpDigit3.loadTexture('displayui', hp % 10);
        this.hpDigit3.visible = true;
    }
    else {
        this.hpDigit1.loadTexture('displayui', Math.floor(hp / 10));
        this.hpDigit2.loadTexture('displayui', hp % 10);
        this.hpDigit3.visible = false;
    }
    this.game.add.tween(this.widthHp).to( { width: hp / this.info.maxHp * 50 }, 200, Phaser.Easing.Linear.None, true);
    this.info.hp = hp;
}

function doShowHpBar(bool) {
    if (typeof bool === 'undefined') bool = true;
    this.hpBarElement.forEach(function (element) {
        element.visible = bool;
    });
    if (bool) this.updateHp(this.info.hp);
}

function showEnemyHpChangeNumber(hpChange) {
    let hpChangeNumber;
    // if (hpChange !== 0) {
        hpChangeNumber = game.add.group();
        let xPos = 0;
        let absChange = Math.abs(hpChange);
        if (absChange >= 100) {
            hpChangeNumber.create(xPos, 0, 'displayui', Math.floor(absChange / 100));
            xPos += 4;
        }
        if (absChange >= 10) {
            hpChangeNumber.create(xPos, 0, 'displayui', Math.floor(absChange / 10));
            xPos += 4;
        }
        hpChangeNumber.create(xPos, 0, 'displayui', Math.floor(absChange % 10));
    // }
    if (hpChange > 0) {
        hpChangeNumber.children.forEach(function (number) {
            number.tint = 0x00ff00;
        });
        hpChangeNumber['effectType'] = 'heal';
    }
    else {
        hpChangeNumber.children.forEach(function (number) {
            number.tint = 0xff0000;
        });
        hpChangeNumber['effectType'] = 'damage';
    }
    hpChangeNumber.scale.setTo(2.0);
    hpChangeNumber.position.setTo(this.x+this.sprite.x, this.y+this.sprite.y);
    hpChangeNumber.visible = false;
    this.effectNumbers.push(hpChangeNumber);
    this.animateEffectNumbers();
}

function animateEnemyEffectNumber() {
    if (this.effectNumbers.length > 0 && !this.isAnimatingNumber) {
        let enemy = this;
        let number = this.effectNumbers.shift();
        number.visible = true;

        let destinationY, destinationX, tween, tween2, tween3;
        switch (number['effectType']) {
            case 'damage':
                destinationY = number.y - 32;
                destinationX = number.x + 48;
                tween = this.game.add.tween(number).to({y: destinationY}, 500, Phaser.Easing.Exponential.Out, true);
                tween2 = this.game.add.tween(number).to({x: destinationX}, 1500, Phaser.Easing.Linear.None, true);

                this.isAnimatingNumber = true;
                tween.onComplete.add(function () {
                    tween3 = enemy.game.add.tween(number).to({y: destinationY + 32}, 1000, Phaser.Easing.Bounce.Out, true);
                    tween3.onComplete.add(removeNumber);
                    startNextAnimation();
                });
                break;

            case 'heal':
                destinationY = number.y - 32;
                tween = this.game.add.tween(number).to( { y: destinationY }, 1000, Phaser.Easing.Linear.None, true);
                this.isAnimatingNumber = true;
                game.time.events.add(500, startNextAnimation, enemy);
                tween.onComplete.add(removeNumber);
                break;
        }

        function removeNumber() {
            number.kill();
            number.destroy(true, false);
        }

        function startNextAnimation() {
            enemy.isAnimatingNumber = false;
            enemy.animateEffectNumbers();
        }
    }
}

function animateEnemyCardUseOnPlayer(card, player) {
    let game = this.game;
    let enemy = this;
    let originX = enemy.sprite.x, originY = enemy.sprite.y;
    let tween1 = game.add.tween(enemy.sprite).to( { x: originX+8 }, 100, Phaser.Easing.Linear.None, true);
    game.add.tween(enemy.shadow).to( { x: originX+8 }, 100, Phaser.Easing.Linear.None, true);
    tween1.onComplete.add(function () {
        let tween2 = game.add.tween(enemy.sprite).to({ x: originX }, 100, Phaser.Easing.Linear.None, true);
        game.add.tween(enemy.shadow).to({ x: originX }, 100, Phaser.Easing.Linear.None, true);
        tween2.onComplete.add(shootCard);
    });

    function shootCard() {
        let cardMini = new CardMini(game, undefined, 'cardMini', false, false, Phaser.Physics.ARCADE, card);
        cardMini.position.setTo(enemyPositions[enemy.posNum].x * enemy.scale.x + originX, enemyPositions[enemy.posNum].y * enemy.scale.y + originY);
        cardMini.spin = true;
        let tween = game.add.tween(cardMini).to({x: player.x + 16, y: player.y + 24}, 300, Phaser.Easing.Linear.None, true);
        tween.onComplete.add(function () {
            cardMini.kill();
            cardMini.destroy(true, false);
            processNextMessage();
        });
    }
}

function enemyStandsOut() {
    game.add.tween(this.sprite).to( { x: this.spriteXPosition-32 }, 300, Phaser.Easing.Linear.None, true);
    game.add.tween(this.shadow).to( { x: this.spriteXPosition-32 }, 300, Phaser.Easing.Linear.None, true);
    game.time.events.add(500, processNextMessage, this);
}

function enemyStandsBack() {
    game.add.tween(this.sprite).to( { x: this.spriteXPosition }, 300, Phaser.Easing.Linear.None, true);
    game.add.tween(this.shadow).to( { x: this.spriteXPosition }, 300, Phaser.Easing.Linear.None, true);
    game.time.events.add(500, processNextMessage, this);
}

function enemyDropDead() {
    let tween = game.add.tween(this.sprite).to( { angle: 90, y: this.sprite.y+6}, 500, Phaser.Easing.Exponential.Out, true);
    tween.onComplete.add(processNextMessage);
}

function updateEnemy(enemyInfo) {
    this.info = enemyInfo;
    this.updateHp(enemyInfo.hp);
}

function update() {
    this.hpBarFill.updateCrop();
}

// -- user code here --

/* --- start generated code --- */

// Generated by  1.5.1 (Phaser v2.6.2)


/**
 * Enemy.
 * @param {Phaser.Game} aGame A reference to the currently running game.
 * @param {Phaser.Group} aParent The parent Group (or other {@link DisplayObject}) that this group will be added to.
    If undefined/unspecified the Group will be added to the {@link Phaser.Game#world Game World}; if null the Group will not be added to any parent.
 * @param {string} aName A name for this group. Not used internally but useful for debugging.
 * @param {boolean} aAddToStage If true this group will be added directly to the Game.Stage instead of Game.World.
 * @param {boolean} aEnableBody If true all Sprites created with {@link #create} or {@link #createMulitple} will have a physics body created on them. Change the body type with {@link #physicsBodyType}.
 * @param {number} aPhysicsBodyType The physics body type to use when physics bodies are automatically added. See {@link #physicsBodyType} for values.
 */
function Enemy(aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType, posNum, enemyInfo) {

	Phaser.Group.call(this, aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType);

	this.posNum = posNum;
	this.info = enemyInfo;
    let x = enemyPositions[posNum].x;
    let y = enemyPositions[posNum].y;

    this.shadow = this.game.add.sprite(8.0, 15.0, 'entity', 'shadow', this);
    this.shadow.scale.setTo(1, 1);
    this.shadow.anchor.setTo(0.5);
    this.sprite = this.game.add.sprite(8.0, 8.0, 'entity', enemyInfo.name.toLowerCase(), this);
    this.sprite.anchor.setTo(0.5);
    this.spriteXPosition = this.sprite.position.x;
    if (this.sprite.width > 16) this.shadow.scale.setTo(1.5, 1.0);

    this.hpBar = this.game.add.sprite(8.0, this.shadow.y+4, 'displayui', 'bar', this);
    this.hpBar.anchor.setTo(0.5, 0.0);

    this.hpDigit3 = this.game.add.sprite(this.hpBar.x + this.hpBar.width/2 + 11, this.hpBar.y, 'displayui', '0', this);
    this.hpDigit3.tint = 0xdbc0b4;

    this.hpDigit2 = this.game.add.sprite(this.hpBar.x + this.hpBar.width/2 + 7, this.hpBar.y, 'displayui', '0', this);
    this.hpDigit2.tint = 0xdbc0b4;

    this.hpDigit1 = this.game.add.sprite(this.hpBar.x + this.hpBar.width/2 + 3, this.hpBar.y, 'displayui', '0', this);
    this.hpDigit1.tint = 0xdbc0b4;

    let bmd = this.game.add.bitmapData(50, 8);
    bmd.ctx.beginPath();
    bmd.ctx.rect(0, 0, 50, 8);
    bmd.ctx.fillStyle = '#447733';
    bmd.ctx.fill();
    this.widthHp = new Phaser.Rectangle(0, 0, bmd.width, bmd.height);
    this.totalHp = bmd.width;
    this.hpBarFill = this.game.add.sprite((x + this.hpBar.x+1 - this.hpBar.width/2)*2, (y + this.hpBar.y+1)*2, bmd);
    this.hpBarFill.cropEnabled = true;
    this.hpBarFill.crop(this.widthHp);

    this.hpBarElement = [this.hpBar, this.hpDigit1, this.hpDigit2, this.hpDigit3, bmd, this.hpBarFill];

    this.border = game.add.graphics(0, 0, this);
    this.border.lineStyle(2, 0xFFFF00, 1);
    if (this.sprite.width > 16)
        this.border.drawRoundedRect(-10, -10, this.sprite.width, this.sprite.height, 5);
    else
        this.border.drawRoundedRect(-1, -1, this.sprite.width + 2, this.sprite.height + 2, 5);
    this.border.visible = false;

    this.scale.setTo(2, 2);
    this.position.setTo(x * this.scale.x, y * this.scale.y);

    this.updateHp(enemyInfo.hp);
    this.effectNumbers = [];
}

/** @type Phaser.Group */
let Enemy_proto = Object.create(Phaser.Group.prototype);
Enemy.prototype = Enemy_proto;
Enemy.prototype.constructor = Enemy;

/* --- end generated code --- */
// -- user code here --
Enemy.prototype.update = update;
Enemy.prototype.doShowHpBar = doShowHpBar;
Enemy.prototype.updateHp = updateEnemyHp;
Enemy.prototype.updateEnemy = updateEnemy;
Enemy.prototype.showHpChangeNumber = showEnemyHpChangeNumber;
Enemy.prototype.animateEffectNumbers = animateEnemyEffectNumber;
Enemy.prototype.animateCardUseOnPlayer = animateEnemyCardUseOnPlayer;
Enemy.prototype.standOut = enemyStandsOut;
Enemy.prototype.standBack = enemyStandsBack;
Enemy.prototype.dropDead = enemyDropDead;