package beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameSession
{
	public GameSession() throws Exception
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
		for (counter = 0; counter < MAX_SESSIONS && gameSessions.containsKey(codeListInInt[codeListCurrentIndex++]); counter++)
		{
			if (codeListCurrentIndex == MAX_SESSIONS-1) codeListCurrentIndex = 0;
		}
		if (counter == MAX_SESSIONS - 1)
		{
			throw new Exception("Maximum number of game sessions reached, cannot create new session.");
		}
		code = codeListInInt[codeListCurrentIndex-1];
		gameSessions.put(code, this);
		
		connectedUsers = new ArrayList<User>();
		connectedDisplays = new ArrayList<Display>();
	}
    
    private int code;
    private ArrayList<User> connectedUsers;
    private ArrayList<Display> connectedDisplays;
    
    /* 
     * static fields
    */
	// All game sessions.
    private static Map<Integer, GameSession> gameSessions = new HashMap<Integer, GameSession>();
    public static int[] codeListInInt = null;
	static int codeListCurrentIndex = 0;
	// how many alphabets do we use. It determines the maximum of concurrent sessions
	// e.g. using 8 alphabets can create maximum of 4096 codes
	static final int ALPHABET_RANGE = 8; 
	static final int MAX_SESSIONS = (int) Math.pow(ALPHABET_RANGE, 4);
    
	public int getCode()
	{
		return code;
	}
	
	public String getCodeString()
	{
		return intToCode(code);
	}
	
	public void setCode(int code)
	{
		this.code = code;
	}
	
	public static GameSession getGameSessionByCode(int code)
	{
		return gameSessions.get(code);
	}
	
	public static GameSession getGameSessionByCodeString(String code)
	{
		return gameSessions.get(codeToInt(code));
	}
	
	public static boolean gameSessionExist(int code)
	{
		return gameSessions.containsKey(code);
	}
	
	// limited to 4 user per session
	public void addUser(User user)
	{
		if (!connectedUsers.contains(user) && connectedUsers.size() < 4)
		{
			connectedUsers.add(user);
		}
	}
	
	public void removeUser(User user)
	{
		if (connectedUsers.contains(user))
		{
			connectedUsers.remove(user);
		}
	}
	
	public boolean isFull()
	{
		return connectedUsers.size() >= 4;
	}
	
	public void addDisplay(Display display)
	{
		if (!connectedDisplays.contains(display))
		{
			connectedDisplays.add(display);
		}
	}
	
	public void removeDisplay(Display display)
	{
		if (connectedDisplays.contains(display))
		{
			connectedDisplays.remove(display);
		}
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
		return (code.charAt(0) - 65) * (int) Math.pow(ALPHABET_RANGE, 3) + 
				(code.charAt(1) - 65) * (int) Math.pow(ALPHABET_RANGE, 2) + 
				(code.charAt(2) - 65) * ALPHABET_RANGE + 
				(code.charAt(3) - 65); 
	}
	
	public static boolean isCodeValid(int code)
	{
		return code >= 0 && code < MAX_SESSIONS;
	}
}
