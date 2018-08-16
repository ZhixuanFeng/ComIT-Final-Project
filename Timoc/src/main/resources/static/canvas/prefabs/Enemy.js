
// -- user code here --
function enemyClicked() {
    this.isSelected = !this.isSelected;
    if (this.isSelected) {
        this.graphics = game.add.graphics();
        this.graphics.lineStyle(4, 0xFFFF00, 1);
        this.graphics.drawRoundedRect(this.sprite.x * this.scale.x - 3, this.sprite.y * this.scale.y - 3, this.borderWidth, this.borderHeight, 10);
    }
    else {
        this.graphics.destroy();
    }
    setEffects();
}
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
function Enemy(aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType, x, y, enemyInfo) {
	
	Phaser.Group.call(this, aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType);
    this.sprite = this.game.add.button(x, y, 'entity', enemyClicked, this, null, enemyInfo.name, null, null, this);

    this.scale.setTo(2, 2);

    this.borderWidth = this.sprite.width * this.scale.x + 6;
    this.borderHeight = this.sprite.height * this.scale.y + 6;
}

/** @type Phaser.Group */
let Enemy_proto = Object.create(Phaser.Group.prototype);
Enemy.prototype = Enemy_proto;
Enemy.prototype.constructor = Enemy;

/* --- end generated code --- */
// -- user code here --
