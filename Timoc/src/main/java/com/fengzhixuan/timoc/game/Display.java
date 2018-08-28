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

    // true: selected; false: not selected
    private boolean[] cardStates = new boolean[5];
    private boolean[] playerStates = new boolean[4];
    private boolean[] enemyStates = new boolean[4];

    // 0-4:cards;  6-9:players;  10-13:enemies;  15:invisible
    private byte cursorPosition = 2;

    private byte numOfCards = 0;
    private byte numOfPlayers = 0;
    private byte numOfEnemies = 0;

    public Display()
    {
        state = displayState.SelectingCards;
    }

    // reset everything
    public void reset(byte numOfCards, byte numOfPlayers, byte numOfEnemies)
    {
        state = displayState.SelectingCards;
        cardStates = new boolean[5];
        playerStates = new boolean[4];
        enemyStates = new boolean[4];
        cursorPosition = numOfCards > 0 ? (byte)(numOfCards / 2) : 15;
    }

    // first byte: card states; second byte: player and enemy states; third byte: cursor position
    public byte[] controllerInput(int buttonCode)
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
                }
                else  // selecting targets
                {
                    if (cursorPosition == 9 || cursorPosition == 13) return null;
                    cursorPosition--;
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
                if (state == displayState.SelectingCards)
                {
                    // TODO: check if player needs to select target
                    state = displayState.SelectingTargets;
                    cursorPosition = 5;
                }
                else  // selecting targets
                {

                }
                break;
            case 6:  // cancel
                reset(numOfCards, numOfPlayers, numOfEnemies);
                break;
            case 7:  // replace
                // TODO: check if player can still replace and do replace
                break;
            case 8:  // discard
                // TODO: do discard
                break;
            case 0:  // next

                return null;
        }

        return toBytes();
    }

    public byte[] toBytes()
    {
        byte[] bytes = new byte[3];
        bytes[0] = (byte)((cardStates[0]?1<<7:0) + (cardStates[1]?1<<6:0) + (cardStates[2]?1<<5:0) +
                (cardStates[3]?1<<4:0) + (cardStates[4]?1<<3:0));
        bytes[1] = (byte)((playerStates[0]?1<<7:0) + (playerStates[1]?1<<6:0) + (playerStates[2]?1<<5:0) +
            (playerStates[3]?1<<4:0) + (enemyStates[0]?1<<3:0) + (enemyStates[1]?1<<2:0) +
            (enemyStates[2]?1<<1:0) + (enemyStates[3]?1:0));
        bytes[2] = cursorPosition;
        return bytes;
    }
}
