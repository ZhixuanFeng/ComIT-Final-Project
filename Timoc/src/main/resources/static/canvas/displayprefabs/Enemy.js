let enemyPositions = [{x:240, y:96}, {x:240, y:128}, {x:240, y:160}, {x:240, y:192}];

function updateEnemyHp(hp) {
    if (hp >= 100) {
        this.hpDigit1.loadTexture('displayui', Math.floor(hp / 100));
        this.hpDigit2.loadTexture('displayui', Math.floor(hp / 10));
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

function updateEnemy(enemyInfo) {
    this.updateHp(enemyInfo.hp);
    this.info = enemyInfo;
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

	this.info = enemyInfo;
    let x = enemyPositions[posNum].x;
    let y = enemyPositions[posNum].y;

    let shadow = this.game.add.sprite(8.0, 15.0, 'entity', 'shadow', this);
    shadow.scale.setTo(1, 1);
    shadow.anchor.setTo(0.5);
    this.sprite = this.game.add.sprite(8.0, 8.0, 'entity', enemyInfo.name.toLowerCase(), this);
    this.sprite.anchor.setTo(0.5);
    if (this.sprite.width > 16) shadow.scale.setTo(1.5, 1.0);

    this.hpBar = this.game.add.sprite(8.0, shadow.y+4, 'displayui', 'bar', this);
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

    this.scale.setTo(2, 2);

    this.position.setTo(x * this.scale.x, y * this.scale.y);

    this.updateHp(enemyInfo.hp);
}

/** @type Phaser.Group */
let Enemy_proto = Object.create(Phaser.Group.prototype);
Enemy.prototype = Enemy_proto;
Enemy.prototype.constructor = Enemy;

/* --- end generated code --- */
// -- user code here --
Enemy.prototype.update = update;
Enemy.prototype.updateHp = updateEnemyHp;
Enemy.prototype.updateEnemy = updateEnemy;