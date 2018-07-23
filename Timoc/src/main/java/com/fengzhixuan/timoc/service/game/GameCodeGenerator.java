package com.fengzhixuan.timoc.service.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GameCodeGenerator
{
    // how many alphabets do we use. It determines the maximum of concurrent sessions
    // e.g. using 8 alphabets allows maximum of 4096 codes/games
    public static final int ALPHABET_RANGE = 8;
    public static final int MAX_SESSIONS = (int) Math.pow(ALPHABET_RANGE, 4);

    public static int[] codeListInInt = null;
    private static int codeListCurrentIndex = 0;

    public static int getNextCode()
    {
        // generate all possible codes and shuffle them, only run when first game session is created
        if (codeListInInt == null)
        {
            codeListInInt = new int[MAX_SESSIONS];
            Random randomGenerator = new Random();
            for (int i = 0; i < MAX_SESSIONS; i++){
                codeListInInt[i] = randomGenerator.nextInt(MAX_SESSIONS);
            }
            Collections.shuffle(Arrays.asList(codeListInInt));
        }

        // scan through the code list to find an available code
        int counter;
        for (counter = 0; counter < MAX_SESSIONS && Game.gameCodeExist(codeListInInt[codeListCurrentIndex++]); counter++)
        {
            if (codeListCurrentIndex == MAX_SESSIONS-1) codeListCurrentIndex = 0;
        }
        if (counter == MAX_SESSIONS - 1)
        {
            // Maximum number of game sessions reached, cannot create new session
            return -1;
        }
        return codeListInInt[codeListCurrentIndex-1];
    }

    // convert an integer into four-letter code, assuming 0 <= i < 456976
    public static String intToCode(int i)
    {
        int firstLetterInt = i / (int) Math.pow(ALPHABET_RANGE, 3);
        char[] firstLetter = Character.toChars(firstLetterInt + 65);
        i = i % (int) Math.pow(ALPHABET_RANGE, 3);
        int secondLetterInt = i / (int) Math.pow(ALPHABET_RANGE, 2);
        char[] secondLetter = Character.toChars(secondLetterInt + 65);
        i = i % (int) Math.pow(ALPHABET_RANGE, 2);
        int thirdLetterInt = i / ALPHABET_RANGE;
        char[] thirdLetter = Character.toChars(thirdLetterInt + 65);
        i = i % ALPHABET_RANGE;
        int fourthLetterInt = i;
        char[] fourthLetter = Character.toChars(fourthLetterInt + 65);

        return "" + firstLetter[0] + secondLetter[0] + thirdLetter[0] + fourthLetter[0];
    }

    // convert four-letter code into integer, assuming code is four letters long
    public static int codeToInt(String code)
    {
        return  code == null ? 0 : (code.charAt(0) - 65) * (int) Math.pow(ALPHABET_RANGE, 3) +
                (code.charAt(1) - 65) * (int) Math.pow(ALPHABET_RANGE, 2) +
                (code.charAt(2) - 65) * ALPHABET_RANGE +
                (code.charAt(3) - 65);
    }

    public static boolean isCodeValid(int code)
    {
        return code >= 0 && code < MAX_SESSIONS;
    }
}
