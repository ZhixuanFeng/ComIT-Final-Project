package com.fengzhixuan.timoc.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameCodeGenerator
{
    // how many alphabets do we use. It determines the maximum of concurrent sessions
    // e.g. using 8 alphabets allows maximum of 4096 codes/games
    public static final int ALPHABET_RANGE = 8;
    public static final int MAX_SESSIONS = (int) Math.pow(ALPHABET_RANGE, 4);

    public static int[] codeListInInt = null;
    private static int codeListCurrentIndex = 0;

    public static int getNextCodeInt()
    {
        // generate all possible codes and shuffle them, only run when first game session is created
        if (codeListInInt == null)
        {
            codeListInInt = new int[MAX_SESSIONS];
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < MAX_SESSIONS; i++) list.add(i);
            Collections.shuffle(list);
            for (int i = 0; i < MAX_SESSIONS; i++) codeListInInt[i] = list.get(i);
        }

        // scan through the code list to find an available code
        codeListCurrentIndex++;
        if (codeListCurrentIndex == MAX_SESSIONS-1) codeListCurrentIndex = 0;
        int counter;
        for (counter = 0; counter < MAX_SESSIONS && (Game.gameCodeExist(codeListInInt[codeListCurrentIndex]) || Room.getRoomByCode(codeListInInt[codeListCurrentIndex]) != null); counter++)
        {
            codeListCurrentIndex++;
            if (codeListCurrentIndex == MAX_SESSIONS-1) codeListCurrentIndex = 0;
        }
        if (counter == MAX_SESSIONS - 1)
        {
            // Maximum number of game sessions reached, cannot create new session
            return -1;
        }
        return codeListInInt[codeListCurrentIndex];
    }

    public static String getNextCode()
    {
        return intToString(getNextCodeInt());
    }

    // convert an integer into four-letter code, assuming 0 <= i < 456976
    public static String intToString(int i)
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
    public static int stringToInt(String code)
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

    public static boolean isCodeValid(String code)
    {
        int biggestLetter = 65 + ALPHABET_RANGE - 1;
        return code != null && code.length() == 4 &&
                code.charAt(0) >= 65 && code.charAt(0) <= biggestLetter &&
                code.charAt(1) >= 65 && code.charAt(1) <= biggestLetter &&
                code.charAt(2) >= 65 && code.charAt(2) <= biggestLetter &&
                code.charAt(3) >= 65 && code.charAt(3) <= biggestLetter;

    }
}
