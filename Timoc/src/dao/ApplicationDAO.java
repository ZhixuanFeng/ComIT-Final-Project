package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import beans.Account;

public class ApplicationDAO 
{
	
	public ApplicationDAO()
	{
	}
	
	// if successful, return affected row number, otherwise 0 - error, -1 - username exists, -2 email exists
	public int registerUser(Account newAccount, Connection connection) 
	{
		int returnCode = 0; 
		try {
			// check if username exists
			String sql = "SELECT 1 FROM player_account WHERE username=?";
			// set parameters with PreparedStatement
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, newAccount.getUsername());
			// execute the statement and check whether username exists
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				return -1;
			}
			set.close();

			// check if email exists
			sql = "SELECT 1 FROM player_account WHERE email=?";
			// set parameters with PreparedStatement
			statement = connection.prepareStatement(sql);
			statement.setString(1, newAccount.getUsername());
			// execute the statement and check whether email exists
			set = statement.executeQuery();
			if (set.next())
			{
				return -2;
			}
			set.close();
			
			
			// create new player table
			String insertPlayerQuery = "INSERT INTO player (name) VALUES(?)";
			statement = connection.prepareStatement(insertPlayerQuery);
			statement.setString(1, newAccount.getUsername());
			// execute the statement
			returnCode = statement.executeUpdate();
			
			// insert new account
			String insertAccountQuery = "INSERT INTO player_account (player_id, username, password, email) "
					+ "VALUES((SELECT id from player WHERE name=?),"
					+ "(SELECT name from player WHERE name=?),"
					+ "?,?)";
			// set parameters with PreparedStatement
			statement = connection.prepareStatement(insertAccountQuery);
			statement.setString(1, newAccount.getUsername());
			statement.setString(2, newAccount.getUsername());
			statement.setString(3, newAccount.getPassword());
			statement.setString(4, newAccount.getEmail());
			// execute the statement
			returnCode = statement.executeUpdate();

		} catch (SQLException exception) 
		{
			exception.printStackTrace();
			return 0;
		}
		return returnCode;
	}
	
	// check if username and password pair is valid and return the account id
	public int validateUser(String username, String password, Connection connection) 
	{
		int id = 0;  // 0-invalid, >0 -valid,  -1 - error
		try 
		{
			// write the select query
			String sql = "SELECT * FROM player_account WHERE username=? AND password=?";

			// set parameters with PreparedStatement
			java.sql.PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			statement.setString(2, password);

			// execute the statement and check whether user exists
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				id = set.getInt(1);
			}
		} catch (SQLException exception)
		{
			exception.printStackTrace();
			return -1;
		}
		
		return id;
	}
	
	// returns account id
	public int getAccountID(String username, Connection connection)
	{
		int id = 0;
		try 
		{
			// write the select query
			String sql = "SELECT 1 FROM player_account WHERE username=?";

			// set parameters with PreparedStatement
			java.sql.PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			statement.setString(1, username);
			// execute the statement and check whether user exists
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				id = set.getInt(1);
			}
		} catch (SQLException exception)
		{
			exception.printStackTrace();
			return -1;
		}
		return id;
	}
}