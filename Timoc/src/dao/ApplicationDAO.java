package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import beans.Account;

public class ApplicationDAO {
	
	public static int[] codeListInInt = null;
	static int codeListCurrentIndex = 0;
	// how many alphabets do we use. It determines the maximum of concurrent sessions
	// e.g. using 8 alphabets can create maximum of 4096 codes
	static final int ALPHABET_RANGE = 8; 
	static final int MAX_SESSIONS = (int) Math.pow(ALPHABET_RANGE, 4);
	
	public ApplicationDAO()
	{
		// generate all possible codes and shuffle them
		int length = MAX_SESSIONS;
		if (codeListInInt == null)
		{
			codeListInInt = new int[length];
	        Random randomGenerator = new Random();
	        for (int i = 0; i < codeListInInt.length; ++i){
	        	codeListInInt[i] = randomGenerator.nextInt(length);
	        }
	        Collections.shuffle(Arrays.asList(codeListInInt));
		}
	}

	public String generateSessionCode(Connection connection) {
		// take a code from the list
		int codeInInt = codeListInInt[codeListCurrentIndex];
		codeListCurrentIndex++;
		if (codeListCurrentIndex == MAX_SESSIONS-1) codeListCurrentIndex = 0;
		
		int row = 0;
		try
		{
			// make sure this code is not used in database
			String sql = "SELECT 1 FROM game_session WHERE connect_code_int = " + codeInInt;
			Statement statement = connection.createStatement();
			ResultSet set = statement.executeQuery(sql);
			int counter = 1; // once counter reaches MAX_SESSIONS, exit while to avoid infinite loop
			while (set.next() && counter < MAX_SESSIONS)
			{
				// If there is a code is already in use, use the next one.
				codeInInt = codeListInInt[codeListCurrentIndex];
				codeListCurrentIndex++;
				sql = "SELECT 1 FROM game_session WHERE connect_code_int = " + codeInInt;
				set.close();
				set = statement.executeQuery(sql);
				counter++;
				if (counter == MAX_SESSIONS) // all codes are checked and are in use
				{
					return null;
				}
			}
			
			// create new game_session entry in databases, store the integer value of the code to save space
			sql = "INSERT INTO game_session (connect_code_int)" + 
					"VALUES (?)";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, codeInInt);
			row = preparedStatement.executeUpdate();
		} catch (SQLException exception)
		{
			// TODO: handle exception
			exception.printStackTrace();
			return null;
		}
		
		if (row == 0)
		{
			return null;
		}
		return intToCode(codeInInt);
	}
	
	// returns the row number of the session updated
	public int connectToSession(Connection connection, String code)
	{
		int codeInInt = codeToInt(code);
		if (codeInInt < 0 || codeInInt > MAX_SESSIONS)
		{
			return 0;//invalid code
		}
		
		int row = 0;
		try {
			String sql = "UPDATE game_session "
					+ "SET connected_players = connected_players + 1 "
					+ "WHERE connect_code_int = (?)";
			PreparedStatement preparedStatement;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, codeInInt);
			row = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			row = -1;
		}
		
		return row;
	}
	
	public int registerUser(Account newAccount, Connection connection) {
		int rowsAffected = 0;

		try {
			// write the insert query
			String insertQuery = "insert into player_account values(?,?,?)";

			// set parameters with PreparedStatement
			java.sql.PreparedStatement statement = connection.prepareStatement(insertQuery);
			statement.setString(1, newAccount.getUsername());
			statement.setString(2, newAccount.getPassword());
			statement.setString(3, newAccount.getEmail());

			// execute the statement
			rowsAffected = statement.executeUpdate();

		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return rowsAffected;
	}
	
	// check if username and password pair is valid and if the account is already online
	public int validateUser(String username, String password, Connection connection) 
	{
		int returnCode = 0;  // 0-invalid, 1-valid, 2-is already online
		try 
		{
			// write the select query
			String sql = "select 1 from users where username=? and password=?";

			// set parameters with PreparedStatement
			java.sql.PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			statement.setString(2, password);

			// execute the statement and check whether user exists
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				returnCode = set.getBoolean("is_online") ? 2: 1;
			}
		} catch (SQLException exception)
		{
			exception.printStackTrace();
		}
		return returnCode;
	}
	
	
	
	
	
	
	
	
	// convert an integer into four-letter code, assuming 0 < i < 456976
	String intToCode(int i)
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
	int codeToInt(String code)
	{
		return (code.charAt(0) - 65) * (int) Math.pow(ALPHABET_RANGE, 3) + 
				(code.charAt(1) - 65) * (int) Math.pow(ALPHABET_RANGE, 2) + 
				(code.charAt(2) - 65) * ALPHABET_RANGE + 
				(code.charAt(3) - 65); 
	}
}