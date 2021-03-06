
// -- user code here --
let selectedCards = [];
let selectedEffectAmount = {'attack': 0, 'block': 0, 'heal': 0, 'mana': 0, 'aoe': 0, 'draw': 0, 'revive': 0, 'hate': 0};

function addSelectedCard(card) {
    selectedCards.push(card);
}

function removeSelectedCard(card) {
    selectedCards = selectedCards.filter(c => c.info.indecks !== card.info.indecks);
}

function clearCardSelection() {
    selectedCards = [];
}

function updateSelectedEffectAmount() {
    selectedEffectAmount = {'attack': 0, 'block': 0, 'heal': 0, 'mana': 0, 'aoe': 0, 'draw': 0, 'revive': 0, 'hate': 0};
    selectedCards.forEach(function (card) {
        effectNames.forEach(function (effect) {
            if (typeof card[effect] !== 'undefined') {
                selectedEffectAmount[effect] += card[effect];
            }
        });
    });
}

/* --- start generated code --- */

// Generated by  1.5.1 (Phaser v2.6.2)


/**
 * Summary.
 * @param {Phaser.Game} aGame A reference to the currently running game.
 * @param {Phaser.Group} aParent The parent Group (or other {@link DisplayObject}) that this group will be added to.
    If undefined/unspecified the Group will be added to the {@link Phaser.Game#world Game World}; if null the Group will not be added to any parent.
 * @param {string} aName A name for this group. Not used internally but useful for debugging.
 * @param {boolean} aAddToStage If true this group will be added directly to the Game.Stage instead of Game.World.
 * @param {boolean} aEnableBody If true all Sprites created with {@link #create} or {@link #createMulitple} will have a physics body created on them. Change the body type with {@link #physicsBodyType}.
 * @param {number} aPhysicsBodyType The physics body type to use when physics bodies are automatically added. See {@link #physicsBodyType} for values.
 */
function Summary(aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType) {
	
	Phaser.Group.call(this, aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType);
	
}

/** @type Phaser.Group */
var Summary_proto = Object.create(Phaser.Group.prototype);
Summary.prototype = Summary_proto;
Summary.prototype.constructor = Summary;

/* --- end generated code --- */
// -- user code here --
