let playerPositions = [{x:64, y:80}, {x:64, y:112}, {x:64, y:144}, {x:64, y:176}];

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
function Player(aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType, posNum) {

	Phaser.Group.call(this, aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType);
    this.game.add.sprite(0.0, 26.0, 'entity', 'shadow', this);
	this.sprite = this.game.add.sprite(0.0, 11.0, 'entity', 'knight', this);

	let _nametext = this.game.add.text(this.sprite.width/2, 0.0, 'name', {"font":"20px Arial", "fill":"#FFFFFF"}, this);
	_nametext.anchor.setTo(0.5, 0.0);
	_nametext.scale.setTo(0.3, 0.3);

    this.scale.setTo(2, 2);

    this.position.setTo(playerPositions[posNum].x * this.scale.x, playerPositions[posNum].y * this.scale.y);

}

/** @type Phaser.Group */
let Player_proto = Object.create(Phaser.Group.prototype);
Player.prototype = Player_proto;
Player.prototype.constructor = Player;

/* --- end generated code --- */
// -- user code here --
