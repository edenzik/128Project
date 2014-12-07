/**
 * 
 */
package com.wrangler.load;

import java.sql.SQLException;

import com.wrangler.ui.login.IncorrectPasswordException;
import com.wrangler.ui.login.UserNotFoundException;

/**
 * @author kahliloppenheimer
 *
 */
public class UserDatabase extends Database {

	private static final String USER_DB_NAME = "postgres"; 
	private static final String USER_TABLE_NAME = "users"; 
	protected UserDatabase()
			throws ClassNotFoundException, SQLException {
		super(USER_DB_NAME, HostFactory.createDefaultHost());
	}
	
	/**
	 * Returns true if the passed user_name exists in the given db
	 * 
	 * @param userName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws UserNotFoundException 
	 * @throws IncorrectPasswordException 
	 */
	public boolean verifyUser(String userName, String userPass) throws UserNotFoundException, IncorrectPasswordException, SQLException{
		String NAME_QUERY = String.format("SELECT * FROM users WHERE username='%s'", userName);
		String NAME_AND_PASS_QUERY = String.format("SELECT * FROM users WHERE username='%s' and password='%s'", userName, userPass);
		if(!getDbHelper().exists(NAME_QUERY)) {
			throw new UserNotFoundException();
		}
		else if(!getDbHelper().exists(NAME_AND_PASS_QUERY)) {
			throw new IncorrectPasswordException();
		}
		return true;
	}
	
	/**
	 * Returns true if the user exists
	 * 
	 * @param userName
	 * @return true if user exists
	 * @throws SQLException
	 */
	public boolean userExists(String userName) throws SQLException{
		String NAME_QUERY = String.format("SELECT * FROM users WHERE username='%s'", userName);
		if(getDbHelper().exists(NAME_QUERY)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Creates a new user
	 * 
	 * @param user to be created, their password
	 * @return
	 * @throws SQLException
	 */
	public boolean addUser(String userName, String userPassword) throws SQLException {
		getDbHelper().executeUpdate("INSERT INTO users VALUES('" +  userName + "', '" + userPassword + "')");
		getDbHelper().createDatabase(userName.split("@")[0]);
		return true;
	}

	/**
	 * Given a user name, returns that user's database. Notably this should be changed
	 * to not assume the default host.
	 * 
	 * @param userName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Database getDatabaseForUser(String userName) throws ClassNotFoundException, SQLException {
		// Check for existing cases of e-mail addresses
		if(userName.contains("@")) {
			userName = userName.split("@")[0];
		}
		return DatabaseFactory.createDatabase(userName, HostFactory.createDefaultHost());
	}
	
}
