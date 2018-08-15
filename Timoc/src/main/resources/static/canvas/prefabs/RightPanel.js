// -- user code here --
let attack, heal, mana, aoe, draw, revive, taunt;
let effects;

let selectedCards = [];

function addSelectedCard(card) {
    selectedCards.push(card);
}

function removeSelectedCard(card) {
    selectedCards = selectedCards.filter( c => c.indecks !== card.indecks );
}

function setEffects() {
    effects.forEach(function (effect) {
        effect.icon.visible = false;
        effect.amount.visible = false;
        effect.amount.setText('0');
    });
    console.log(selectedCards);
    selectedCards.forEach(function (card) {
        if (typeof card.attack !== 'undefined') attack.amount.setText(+attack.amount.text + +card.attack);
        if (typeof card.heal !== 'undefined') heal.amount.setText(+heal.amount.text + +card.heal);
        if (typeof card.mana !== 'undefined') mana.amount.setText(+mana.amount.text + +card.mana);
        if (typeof card.aoe !== 'undefined') aoe.amount.setText(+aoe.amount.text + +card.aoe);
        if (typeof card.draw !== 'undefined') draw.amount.setText(+draw.amount.text + +card.draw);
        if (typeof card.revive !== 'undefined') revive.amount.setText(+revive.amount.text + +card.revive);
        if (typeof card.taunt !== 'undefined') taunt.amount.setText(+taunt.amount.text + +card.taunt);
    });

    if (parseInt(aoe.amount.text) > 0) {
        effects.forEach(function (effect) {
            effect.amount.setText(Math.ceil(+effect.amount.text / 2));
        });
        aoe.amount.setText('50%');
    }

    let iconY = 18;
    let textY = 13;
    effects.forEach(function (effect) {
        if (parseInt(effect.amount.text) > 0) {
            effect.icon.visible = true;
            effect.amount.visible = true;
            effect.icon.y = iconY;
            effect.amount.y = textY;
            iconY += 20;
            textY += 20;
        }
    });
}

/* --- start generated code --- */

// Generated by  1.5.1 (Phaser v2.6.2)


/**
 * RightPanel.
 * @param {Phaser.Game} aGame A reference to the currently running game.
 * @param {Phaser.Group} aParent The parent Group (or other {@link DisplayObject}) that this group will be added to.
    If undefined/unspecified the Group will be added to the {@link Phaser.Game#world Game World}; if null the Group will not be added to any parent.
 * @param {string} aName A name for this group. Not used internally but useful for debugging.
 * @param {boolean} aAddToStage If true this group will be added directly to the Game.Stage instead of Game.World.
 * @param {boolean} aEnableBody If true all Sprites created with {@link #create} or {@link #createMulitple} will have a physics body created on them. Change the body type with {@link #physicsBodyType}.
 * @param {number} aPhysicsBodyType The physics body type to use when physics bodies are automatically added. See {@link #physicsBodyType} for values.
 */
function RightPanel(aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType) {
	
	Phaser.Group.call(this, aGame, aParent, aName, aAddToStage, aEnableBody, aPhysicsBodyType);

	let tauntAmount = this.game.add.text(679.0, 133.0, 'A', {"font":"bold 20px Arial"}, this);
    let reviveAmount = this.game.add.text(679.0, 113.0, 'A', {"font":"bold 20px Arial"}, this);
    let drawAmount = this.game.add.text(679.0, 93.0, 'A', {"font":"bold 20px Arial"}, this);
    let aoeAmount = this.game.add.text(679.0, 73.0, 'A', {"font":"bold 20px Arial"}, this);
    let manaAmount = this.game.add.text(679.0, 53.0, 'A', {"font":"bold 20px Arial"}, this);
    let healAmount = this.game.add.text(679.0, 33.0, 'A', {"font":"bold 20px Arial"}, this);
	let attackAmount = this.game.add.text(679.0, 13.0, 'A', {"font":"bold 20px Arial"}, this);
    let tauntIcon = this.game.add.sprite(649.0, 138.0, 'card', 'hate', this);
    let reviveIcon = this.game.add.sprite(649.0, 118.0, 'card', 'revive', this);
    let drawIcon = this.game.add.sprite(649.0, 98.0, 'card', 'draw', this);
    let aoeIcon = this.game.add.sprite(649.0, 78.0, 'card', 'aoe', this);
    let manaIcon = this.game.add.sprite(649.0, 58.0, 'card', 'mana', this);
    let healIcon = this.game.add.sprite(649.0, 38.0, 'card', 'heal', this);
	let attackIcon = this.game.add.sprite(649.0, 18.0, 'card', 'attack', this);

	attack = {icon: attackIcon, amount: attackAmount};
    heal = {icon: healIcon, amount: healAmount};
    mana = {icon: manaIcon, amount: manaAmount};
    aoe = {icon: aoeIcon, amount: aoeAmount};
    draw = {icon: drawIcon, amount: drawAmount};
    revive = {icon: reviveIcon, amount: reviveAmount};
    taunt = {icon: tauntIcon, amount: tauntAmount};
    effects = [attack, heal, mana, aoe, draw, revive, taunt];

    effects.forEach(function (effect) {
        effect.icon.visible = false;
        effect.amount.visible = false;
    });
	
	let _cancelbutton = this.game.add.button(625.0, 225.0, 'ui', cancelSelection, this, 'cancelbutton', 'cancelbutton', 'cancelbuttonpressed', 'cancelbutton', this);
	_cancelbutton.scale.setTo(1.5, 1.5);
	
	let _playbutton = this.game.add.button(625.0, 165.0, 'ui', playCard, this, 'playbutton', 'playbutton', 'playbuttonpressed', 'playbutton', this);
	_playbutton.scale.setTo(1.5, 1.5);
	
	let _discardbutton = this.game.add.button(625.0, 285.0, 'ui', discardCard, this, 'discardbutton', 'discardbutton', 'discardbuttonpressed', 'discardbutton', this);
	_discardbutton.scale.setTo(1.5, 1.5);
	
	let _endturnbutton = this.game.add.button(625.0, 345.0, 'ui', endTurn, this, 'endturnbutton', 'endturnbutton', 'endturnbuttonpressed', 'endturnbutton', this);
	_endturnbutton.scale.setTo(1.5, 1.5);

}

/** @type Phaser.Group */
let RightPanel_proto = Object.create(Phaser.Group.prototype);
RightPanel.prototype = RightPanel_proto;
RightPanel.prototype.constructor = RightPanel;

/* --- end generated code --- */
// -- user code here --
