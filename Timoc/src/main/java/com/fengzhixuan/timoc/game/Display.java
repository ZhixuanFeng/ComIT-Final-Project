package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.game.enums.TargetingMode;
import com.fengzhixuan.timoc.websocket.message.game.DisplayStateMessage;
import com.fengzhixuan.timoc.websocket.message.game.GameIntMessage;
import com.fengzhixuan.timoc.websocket.message.game.GameMessage;
import com.fengzhixuan.timoc.websocket.message.game.MessageType;

public class Display
{
    public enum displayState
    {
        SelectingCards,
        SelectingTargets,
        NotControlling
    }

    private displayState state;
    private Game game;

    // true: selected; false: not selected
    private boolean[] cardStates = new boolean[5];

    // 0-4:cards;  6-9:players;  10-13:enemies;  15:invisible
    private int cursorPosition = 2;

    private int numOfCards = 0;  // number of cards in hand
    private int numOfCardsSelected = 0;  // number of cards selected
    private int numOfPlayers = 0;  // number of players displayed
    private int numOfEnemies = 0;  // number of enemies displayed
    private boolean isAOE = false;

    private TotalSelectedEffects totalSelectedEffects;

    public Display(Game game)
    {
        this.game = game;
        state = displayState.SelectingCards;
    }

    // reset everything
    public void reset(int numOfCards, int numOfPlayers, int numOfEnemies)
    {
        state = displayState.SelectingCards;
        this.numOfCards = numOfCards;
        numOfCardsSelected = 0;
        this.numOfPlayers = numOfPlayers;
        this.numOfEnemies = numOfEnemies;
        cardStates = new boolean[5];
        cursorPosition = numOfCards > 0 ? 0 : 15;
    }

    public void reset(int numOfCards)
    {
        reset(numOfCards, numOfPlayers, numOfEnemies);
    }

    // first int: card states; second int: player and enemy states; third int: cursor position
    public Integer controllerInput(int buttonCode)
    {
        if (state == displayState.NotControlling) return null;

        // when cursorPosition == 15, there's no cards to use, ignore all buttons except next turn button
        if (cursorPosition == 15 && buttonCode != 0) return null;

        switch (buttonCode)
        {
            case 1:  // up
                if (state == displayState.SelectingCards)
                {
                    if (cardStates[cursorPosition]) return null;
                    cardStates[cursorPosition] = true;
                    numOfCardsSelected++;
                }
                else  // selecting targets
                {
                    if (cursorPosition == 6 || cursorPosition == 10) return null;
                    if (cursorPosition < 10)
                    {
                        cursorPosition--;
                    }
                    else
                    {
                        int i;
                        boolean moved = false;
                        for (i = 1; cursorPosition - i > 9; i++)
                        {
                            Enemy enemy = game.getExistingEnemies()[cursorPosition - 10 - i];
                            if (enemy != null && !enemy.isDead())
                            {
                                cursorPosition -= i;
                                moved = true;
                                break;
                            }
                        }
                        if (!moved) return null;
                    }
                }
                break;
            case 2:  // down
                if (state == displayState.SelectingCards)
                {
                    if (!cardStates[cursorPosition]) return null;
                    cardStates[cursorPosition] = false;
                    numOfCardsSelected--;
                }
                else  // selecting targets
                {
                    if (cursorPosition == 9 || cursorPosition == 13) return null;
                    if (cursorPosition < 10 && cursorPosition - 5 < numOfPlayers)
                    {
                        cursorPosition++;
                    }
                    else
                    {
                        int i;
                        boolean moved = false;
                        for (i = 1; cursorPosition + i < 14; i++)
                        {
                            Enemy enemy = game.getExistingEnemies()[cursorPosition - 10 + i];
                            if (enemy != null && !enemy.isDead())
                            {
                                cursorPosition += i;
                                moved = true;
                                break;
                            }
                        }
                        if (!moved) return null;
                    }
                }
                break;
            case 3:  // left
                if (state == displayState.SelectingCards)
                {
                    cursorPosition--;
                    if (cursorPosition < 0) cursorPosition = numOfCards-1;
                }
                else  // selecting targets
                {
                    if (cursorPosition < 10 && cursorPosition > 5) return null;
                    int offset = cursorPosition - 10;
                    if (offset > numOfPlayers-1) offset = numOfPlayers - 1;
                    cursorPosition = 6 + offset;
                }
                break;
            case 4:  // right
                if (state == displayState.SelectingCards)
                {
                    cursorPosition++;
                    if (cursorPosition == numOfCards) cursorPosition = 0;
                }
                else  // selecting targets
                {
                    if (cursorPosition < 14 && cursorPosition > 9 || numOfEnemies == 0 || game.getNumOfAliveEnemies() == 0) return null;
                    int offset = cursorPosition - 6;
                    if (game.getExistingEnemies()[offset] == null)
                    {
                        for (int i = 1; i < 3; i++)
                        {
                            if (offset - i >= 0 && game.getExistingEnemies()[offset-i] != null)
                            {
                                offset -= i;
                                break;
                            }
                            if (offset + i <= 3 && game.getExistingEnemies()[offset+i] != null)
                            {
                                offset += i;
                                break;
                            }
                        }
                    }
                    cursorPosition = 10 + offset;
                }
                break;
            case 5:  // play
                if (state == displayState.SelectingCards)
                {
                    // if card at cursor position is selected, play it along with other selected
                    if (cardStates[cursorPosition])
                    {
                        Player currentPlayer = game.getCurrentPlayer();

                        // check if player should select target
                        totalSelectedEffects = new TotalSelectedEffects(currentPlayer, getSelectedCardsFromPlayer(currentPlayer));
                        isAOE = totalSelectedEffects.isAoe();
                        if (totalSelectedEffects.doNeedToSelectTarget())
                        {
                            state = displayState.SelectingTargets;
                            // if the cards have attack, move cursor to enemy
                            if (totalSelectedEffects.getAttack() > 0 && game.getNumOfAliveEnemies() > 0)
                            {
                                Enemy[] enemyArray = game.getExistingEnemies();
                                int index = 0;
                                while (enemyArray[index] == null || enemyArray[index].isDead()) index++;
                                cursorPosition = 10 + index;
                            }
                            // else move cursor to player
                            else
                            {
                                cursorPosition = 6;
                            }
                        }
                        else
                        {
                            // play cards
                            totalSelectedEffects.setTargetingMode(TargetingMode.Self);
                            game.playerPlaysCard(currentPlayer, totalSelectedEffects);
                        }
                    }
                    // select this card
                    else
                    {
                        cardStates[cursorPosition] = true;
                        numOfCardsSelected++;
                    }
                }
                else  // selecting targets
                {
                    Player currentPlayer = game.getCurrentPlayer();
                    totalSelectedEffects = new TotalSelectedEffects(currentPlayer, getSelectedCardsFromPlayer(currentPlayer));

                    if (currentPlayer.getMana() < totalSelectedEffects.getManaCost())
                    {
                        game.addDisplayMessage(new GameMessage(MessageType.NotEnoughMana));
                        return null; // fail
                    }

                    // get targets
                    isAOE = totalSelectedEffects.isAoe();
                    TargetingMode targetingMode = cursorPosition < 10 ?
                            (isAOE ? TargetingMode.AllPlayers : TargetingMode.Player) :
                            (isAOE ? TargetingMode.AllEnemies : TargetingMode.Enemy);
                    int targetPosition = cursorPosition < 10 ? cursorPosition-6 : cursorPosition-10;
                    totalSelectedEffects.setTargetingMode(targetingMode);
                    totalSelectedEffects.setTargetPosition(targetPosition);

                    // play cards
                    pauseControl();
                    game.playerPlaysCard(game.getCurrentPlayer(), totalSelectedEffects);

                    // auto finish player turn if player has no more cards left
//                    if (currentPlayer.getHandPile().size() == 0) game.finishPlayerTurn();
                }
                break;
            case 6:  // cancel
                if (state == displayState.SelectingCards)
                {
                    // if current card is selected, deselect
                    if (cardStates[cursorPosition])
                    {
                        cardStates[cursorPosition] = false;
                        numOfCardsSelected--;
                    }
                    // otherwise deselect all selected cards
                    else
                    {
                        int prevCursorPos = cursorPosition;
                        reset(numOfCards, numOfPlayers, numOfEnemies);
                        cursorPosition = prevCursorPos;
                    }
                }
                else  // selecting targets
                {
                    state = displayState.SelectingCards;
                    cursorPosition = numOfCards > 0 ? (numOfCards / 2) : 15;
                }
                break;
            case 7:  // replace
                Player currentPlayer = game.getCurrentPlayer();
                // check if player can still replace and do replace
                if (currentPlayer.getReplaceAllowance() > 0)
                {
                    Card card = getCardAtCursorPosition(currentPlayer);
                    int prevCursorPosition = cursorPosition;
                    pauseControl();  // sets cursor to 15
                    game.addDisplayMessage(new GameIntMessage(MessageType.RemoveCardAtPosition, prevCursorPosition));
                    game.playerReplacesCard(currentPlayer, card);  // sends display status
                }
                else
                {
                    game.addDisplayMessage(new GameMessage(MessageType.NoMoreReplace));
                    return null; // fail
                }
                return null;
            case 8:  // discard
                currentPlayer = game.getCurrentPlayer();
                Card card = getCardAtCursorPosition(currentPlayer);
                int prevCursorPosition = cursorPosition;
                pauseControl();  // sets cursor to 15
                game.addDisplayMessage(new GameIntMessage(MessageType.RemoveCardAtPosition, prevCursorPosition));
                game.playerDiscardsCard(currentPlayer, card);  // sends display status

                // auto finish player turn if player has no more cards left
//                if (currentPlayer.getHandPile().size() == 0) game.finishPlayerTurn();
                return null;
            case 0:  // next
                game.finishPlayerTurn();
                return null;
        }

        return toInteger();
    }

    // sum up the display into an integer
    public int toInteger()
    {
        int states = 0;
        states = (cursorPosition << 8) +
                (cardStates[0]?1<<7:0) +
                (cardStates[1]?1<<6:0) +
                (cardStates[2]?1<<5:0) +
                (cardStates[3]?1<<4:0) +
                (cardStates[4]?1<<3:0) +
                (isAOE?1:0);
        return states;
    }

    // get information of the card at cursor position
    private Card getCardAtCursorPosition(Player player)
    {
        int indecks = player.getHandPile().get(cursorPosition);
        return player.getCardByIndecks(indecks);
    }

    // get information of selected cards from player
    private Card[] getSelectedCardsFromPlayer(Player player)
    {
        Card[] hand = player.getHand();
        Card[] selectedCards = new Card[numOfCardsSelected];

        int count = 0;
        for (int i = 0; i < 5; i++)
        {
            if (cardStates[i])
            {
                selectedCards[count] = hand[i];
                count++;
            }
        }
        return selectedCards;
    }

    public void pauseControl()
    {
        state = displayState.NotControlling;
        cursorPosition = 15;
        game.addDisplayMessage(new DisplayStateMessage(toInteger()));
    }
}
