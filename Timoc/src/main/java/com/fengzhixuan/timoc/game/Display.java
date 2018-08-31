package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.game.enums.TargetingMode;
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
                            if (game.getAliveEnemies()[cursorPosition - 10 - i] != null)
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
                    if (cursorPosition < 10)
                    {
                        cursorPosition++;
                    }
                    else
                    {
                        int i;
                        boolean moved = false;
                        for (i = 1; cursorPosition + i < 14; i++)
                        {
                            if (game.getAliveEnemies()[cursorPosition - 10 + i] != null)
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
                    if (cursorPosition == 0) return null;
                    cursorPosition--;
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
                    if (cursorPosition == numOfCards-1) return null;
                    cursorPosition++;
                }
                else  // selecting targets
                {
                    if (cursorPosition < 14 && cursorPosition > 9 || numOfEnemies == 0) return null;
                    int offset = cursorPosition - 6;
                    if (game.getAliveEnemies()[offset] == null)
                    {
                        for (int i = 1; i < 3; i++)
                        {
                            if (offset - i >= 0 && game.getAliveEnemies()[offset-i] != null)
                            {
                                offset -= i;
                                break;
                            }
                            if (offset + i <= 3 && game.getAliveEnemies()[offset+i] != null)
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
                if (numOfCardsSelected == 0) return null;

                if (state == displayState.SelectingCards)
                {
                    Player currentPlayer = game.getCurrentPlayer();

                    // check if player should select target
                    totalSelectedEffects = new TotalSelectedEffects(getSelectedCardsFromPlayer(currentPlayer));
                    isAOE = totalSelectedEffects.isAoe();
                    if (totalSelectedEffects.doNeedToSelectTarget())
                    {
                        state = displayState.SelectingTargets;
                        cursorPosition = 6;
                    }
                    else
                    {
                        // play cards
                        totalSelectedEffects.setTargetingMode(TargetingMode.Self);
                        game.playerPlaysCard(currentPlayer, totalSelectedEffects);
                    }
                }
                else  // selecting targets
                {
                    Player currentPlayer = game.getCurrentPlayer();
                    totalSelectedEffects = new TotalSelectedEffects(getSelectedCardsFromPlayer(currentPlayer));

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
                    game.playerPlaysCard(game.getCurrentPlayer(), totalSelectedEffects);
                }
                break;
            case 6:  // cancel
                if (state == displayState.SelectingCards)
                {
                    reset(numOfCards, numOfPlayers, numOfEnemies);
                }
                else  // selecting targets
                {
                    state = displayState.SelectingCards;
                    cursorPosition = numOfCards > 0 ? (numOfCards / 2) : 15;
                }
                break;
            case 7:  // replace
                if (numOfCardsSelected == 0) return null;
                // check if player can still replace and do replace
                Player currentPlayer = game.getCurrentPlayer();
                if (numOfCardsSelected > currentPlayer.getReplaceAllowance())
                {
                    game.addDisplayMessage(new GameMessage(MessageType.NoMoreReplace));
                    return null; // fail
                }

                Card[] selectedCards = getSelectedCardsFromPlayer(currentPlayer);
                game.playerReplacesCards(currentPlayer, selectedCards);  // sends display status
                return null;
            case 8:  // discard
                if (numOfCardsSelected == 0) return null;
                currentPlayer = game.getCurrentPlayer();
                selectedCards = getSelectedCardsFromPlayer(currentPlayer);
                game.playerDiscardsCards(currentPlayer, selectedCards);  // sends display status
                return null;
            case 0:  // next
                game.finishPlayerTurn();
                return null;
        }

        return toInteger();
    }

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
}
