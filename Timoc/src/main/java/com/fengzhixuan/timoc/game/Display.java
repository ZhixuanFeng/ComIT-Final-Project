package com.fengzhixuan.timoc.game;

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
    private boolean[] playerStates = new boolean[4];
    private boolean[] enemyStates = new boolean[4];

    // 0-4:cards;  6-9:players;  10-13:enemies;  15:invisible
    private int cursorPosition = 2;

    private int numOfCards = 0;  // number of cards in hand
    private int numOfCardsSelected = 0;  // number of cards selected
    private int numOfPlayers = 0;  // number of players displayed
    private int numOfEnemies = 0;  // number of enemies displayed

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
        cursorPosition = numOfCards > 0 ? (numOfCards / 2) : 15;
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
                    cursorPosition--;
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
                    cursorPosition++;
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
                    byte offset = (byte)(cursorPosition - 10);
                    if (offset > numOfPlayers) offset = (byte)(numOfPlayers - 1);
                    cursorPosition = (byte)(6 + offset);
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
                    byte offset = (byte)(cursorPosition - 6);
                    if (offset > numOfEnemies) offset = (byte)(numOfEnemies - 1);
                    cursorPosition = (byte)(10 + offset);
                }
                break;
            case 5:  // play
                if (numOfCardsSelected == 0) return null;
                if (state == displayState.SelectingCards)
                {
                    Player currentPlayer = game.getCurrentPlayer();
                    Card[] hand = currentPlayer.getHand();
                    Card[] selectedCards = new Card[numOfCardsSelected];

                    // get information of selected cards from player
                    int count = 0;
                    for (int i = 0; i < 5; i++)
                    {
                        if (cardStates[i])
                        {
                            selectedCards[count] = hand[i];
                            count++;
                        }
                    }

                    totalSelectedEffects = new TotalSelectedEffects(selectedCards);
                    if (totalSelectedEffects.doNeedToSelectTarget())
                    {
                        state = displayState.SelectingTargets;
                        cursorPosition = 6;
                    }
                    else
                    {
                        // play cards
                    }
                }
                else  // selecting targets
                {
                    // play cards
                }
                break;
            case 6:  // cancel
                reset(numOfCards, numOfPlayers, numOfEnemies);
                break;
            case 7:  // replace
                if (numOfCardsSelected == 0) return null;
                // TODO: check if player can still replace and do replace
                break;
            case 8:  // discard
                if (numOfCardsSelected == 0) return null;
                // TODO: do discard
                break;
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
                numOfCardsSelected;
        return states;
    }
}
