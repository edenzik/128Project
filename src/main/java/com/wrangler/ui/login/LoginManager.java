/**
 * 
 */
package com.wrangler.ui.login;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrangler.load.Database;
import com.wrangler.load.DatabaseFactory;
import com.wrangler.load.UserDatabase;

/**
 * @author kahliloppenheimer
 *
 */
public class LoginManager {
	
	private UserDatabase userDb; 
	private final Logger LOG = LoggerFactory.getLogger(LoginManager.class);
	
	public LoginManager() {
		try {
			userDb = DatabaseFactory.createUserDatabase();
		} catch (ClassNotFoundException e) {
			LOG.error("", e);
		} catch (SQLException e) {
			LOG.error("", e);
		} 
	}
	
	/**
	 * Queries user database to see if a particular user name and pass exist. If so, returns
	 * a User object associated with that that user name and pass which contains a reference
	 * to the user's db.
	 * 
	 * @param userName
	 * @param userPass
	 * @return
	 * @throws UserNotFoundException
	 * @throws IncorrectPasswordException
	 */
	public User login(String userName, String userPass) throws UserNotFoundException, IncorrectPasswordException {
		try {
			if(getUserDb().verifyUser(userName, userPass)) {
				Database db = userDb.getDatabaseForUser(userName);
				return UserFactory.createUser(userName, userPass, db);
			} 
		} catch (UserNotFoundException e) {
			throw e;
		} catch (IncorrectPasswordException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			LOG.error("", e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOG.error("", e);
		}
		// Should theoretically never be reached
		return null;
	}
	
	/**
	 * Queries user database register a user. If a user exists, sends an error.
	 * If not, makes a user.
	 * 
	 * @param userName
	 * @param userPass
	 * @return
	 * @throws UserNotFoundException
	 * @throws IncorrectPasswordException
	 */
	public User register(String userName, String userPass) throws UserAlreadyExistsException {
		try {
			if(getUserDb().userExists(userName)) {
				throw new UserAlreadyExistsException();
			} else {
				getUserDb().addUser(userName, userPass);
				Database db = userDb.getDatabaseForUser(userName);
				return UserFactory.createUser(userName, userPass, db);
			}
		} catch (ClassNotFoundException e) {
			LOG.error("", e);
		} catch (SQLException e) {
			LOG.error("", e);
		}
		// Should theoretically never be reached
		return null;
	}

	/**
	 * @return the userDb
	 */
	public UserDatabase getUserDb() {
		return userDb;
	}

}
