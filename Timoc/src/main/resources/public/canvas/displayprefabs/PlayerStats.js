let statsPositions = [{x:12, y:32}, {x:12, y:64}, {x:12, y:96}, {x:12, y:128}];

function updateHp(hp) {
    if (hp === 100) {
        this.hpDigit1.loadTexture('displayui', 1);
        this.hpDigit2.loadTexture('displayui', 0);
        this.hpDigit3.loadTexture('displayui', 0);
        this.hpDigit1.visible = true;
    }
    else {
        this.hpDigit2.loadTexture('displayui', Math.floor(hp / 10));
        this.hpDigit3.loadTexture('displayui', hp % 10);
        this.hpDigit1.visible = false;
    }
    this.game.add.tween(this.widthHp).to( { width: hp / this.info.maxHp * 50 }, 200, Phaser.Easing.Linear.None, true);
    this.info.hp = hp;
}

function updateMana(mana) {
    if (mana === 100) {
        this.manaDigit1.loadTexture('displayui', 1);
        this.manaDigit2.loadTexture('displayui', 0);
        this.manaDigit3.loadTexture('displayui', 0);
        this.manaDigit1.visible = true;
    }
    else {
        this.manaDigit2.loadTexture('displayui', Math.floor(mana / 10));
        this.manaDigit3.loadTexture('displayui', mana % 10);
        this.manaDigit1.visible = false;
    }
    this.game.add.tween(this.widthMana).to( { width: mana / this.info.maxMana * 50 }, 200, Phaser.Easing.Linear.None, true);
    this.info.mana = mana;
}

function updateHate(hate) {
    if (hate > 99) hate = 99;
    if (hate < 10) {
        this.hateDigit2.visible = false;
        this.hateDigit1.loadTexture('displayui', hate);
    }
    else {
        this.hateDigit2.visible = true;
        this.hateDigit1.loadTexture('displayui', Math.floor(hate / 10));
        this.hateDigit2.loadTexture('displayui', hate % 10);
    }
    this.info.hate = hate;
}

function updateBlock(block) {
    if (block > 99) block = 99;
    if (block < 0) block = 0;
    if (block < 10) {
        this.blockDigit2.visible = false;
        this.blockDigit1.loadTexture('displayui', block);
    }
    else {
        this.blockDigit2.visible = true;
        this.blockDigit1.loadTexture('displayui', Math.floor(block / 10));
        this.blockDigit2.loadTexture('displayui', block % 10);
    }
    this.info.block = block;
}

function updateStats(playerInfo) {
    this.updateHp(playerInfo.hp);
    this.updateMana(playerInfo.mana);
    this.updateHate(playerInfo.hate);
    this.updateBlock(playerInfo.block);
    this.info = playerInfo;
}

function update() {
    this.hpBarFill.updateCrop();
    this.manaBarFill.updateCrop();
}
// -- user code here --

/* --- start generated code --- */

// Generated by  1.5.1 (Phaser v2.6.2)


/**
 * PlayerStats.
 * @param {Phaser.Game} aGame A reference to the currently running game.
 * @param {Phaser.Group} aParent The parent Group (or other {@link DisplayObject}) that this group will be added to.
    If undefined/unspecified the Group will be added to the {@link Phaser.Game#world Game World}; if null the Group will not be added to any parent.
 * @param {string} aName A name for this group. Not used internally but useful for debugging.
 * @param {boolean} aAddToStage If true this group will be added directly to the Game.Stage instead of Game.World.
 * @param {boolean} aEnableBody If true all Sprites created with {@link #create} or {@link #createMulitple} will have a physics body created on them. Change the body type with {@link #physicsBodyType}.
 * @param {number} aPhysicsBodyType The physics body type to use when physics bodies are automatically added. See {@link #physicsBodyType} for values.
 */
function PlayerStats(aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType, posNum, playerInfo) {
	
	Phaser.Group.call(this, aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType);

	this.info = playerInfo;
	let x = statsPositions[posNum].x;
	let y = statsPositions[posNum].y;

    this.manaBar = this.game.add.sprite(4.0, 15.0, 'displayui', 'bar', this);

    this.hpBar = this.game.add.sprite(4.0, 8.0, 'displayui', 'bar', this);

    this.game.add.sprite(4.0, 22.0, 'displayui', 'hatemini', this);

    this.game.add.sprite(20.0, 22.0, 'displayui', 'blockmini', this);

    this.hateDigit2 = this.game.add.sprite(15.0, 23.0, 'displayui', '0', this);
    this.hateDigit2.tint = 0xdbc0b4;

    this.hateDigit1 = this.game.add.sprite(11.0, 23.0, 'displayui', '0', this);
    this.hateDigit1.tint = 0xdbc0b4;

    this.blockDigit2 = this.game.add.sprite(31.0, 23.0, 'displayui', '0', this);
    this.blockDigit2.tint = 0xdbc0b4;

    this.blockDigit1 = this.game.add.sprite(27.0, 23.0, 'displayui', '0', this);
    this.blockDigit1.tint = 0xdbc0b4;

    this.manaDigit3 = this.game.add.sprite(40.0, 16.0, 'displayui', '0', this);
    this.manaDigit3.tint = 0xdbc0b4;

    this.manaDigit2 = this.game.add.sprite(36.0, 16.0, 'displayui', '0', this);
    this.manaDigit2.tint = 0xdbc0b4;

    this.manaDigit1 = this.game.add.sprite(32.0, 16.0, 'displayui', '0', this);
    this.manaDigit1.tint = 0xdbc0b4;

    this.hpDigit3 = this.game.add.sprite(40.0, 9.0, 'displayui', '0', this);
    this.hpDigit3.tint = 0xdbc0b4;

    this.hpDigit2 = this.game.add.sprite(36.0, 9.0, 'displayui', '0', this);
    this.hpDigit2.tint = 0xdbc0b4;

    this.hpDigit1 = this.game.add.sprite(32.0, 9.0, 'displayui', '0', this);
    this.hpDigit1.tint = 0xdbc0b4;

    let bmd = this.game.add.bitmapData(50, 8);
    bmd.ctx.beginPath();
    bmd.ctx.rect(0, 0, 50, 8);
    bmd.ctx.fillStyle = '#447733';
    bmd.ctx.fill();
    this.widthHp = new Phaser.Rectangle(0, 0, bmd.width, bmd.height);
    this.totalHp = bmd.width;
    this.hpBarFill = this.game.add.sprite((x + this.hpBar.x+1)*2, (y + this.hpBar.y+1)*2, bmd);
    this.hpBarFill.cropEnabled = true;
    this.hpBarFill.crop(this.widthHp);

    bmd = this.game.add.bitmapData(50, 8);
    bmd.ctx.beginPath();
    bmd.ctx.rect(0, 0, 50, 8);
    bmd.ctx.fillStyle = '#2b6380';
    bmd.ctx.fill();
    this.widthMana = new Phaser.Rectangle(0, 0, bmd.width, bmd.height);
    this.totalMana = bmd.width;
    this.manaBarFill = this.game.add.sprite((x + this.manaBar.x+1)*2, (y + this.manaBar.y+1)*2, bmd);
    this.manaBarFill.cropEnabled = true;
    this.manaBarFill.crop(this.widthMana);

    this.scale.setTo(2, 2);
    this.position.setTo(x * this.scale.x, y * this.scale.y);

    this.updateHp(playerInfo.hp);
    this.updateMana(playerInfo.mana);
    this.updateHate(playerInfo.hate);
    this.updateBlock(playerInfo.block);
}

/** @type Phaser.Group */
let PlayerStats_proto = Object.create(Phaser.Group.prototype);
PlayerStats.prototype = PlayerStats_proto;
PlayerStats.prototype.constructor = PlayerStats;

/* --- end generated code --- */
// -- user code here --
PlayerStats.prototype.update = update;
PlayerStats.prototype.updateHp = updateHp;
PlayerStats.prototype.updateMana = updateMana;
PlayerStats.prototype.updateHate = updateHate;
PlayerStats.prototype.updateBlock = updateBlock;
PlayerStats.prototype.updateStats = updateStats;