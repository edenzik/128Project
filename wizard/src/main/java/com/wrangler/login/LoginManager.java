/**
 * 
 */
package com.wrangler.login;

import java.sql.SQLException;

import com.wrangler.load.Database;
import com.wrangler.load.DatabaseFactory;
import com.wrangler.load.UserDatabase;

/**
 * @author kahliloppenheimer
 *
 */
public class LoginManager {
	
	private final UserDatabase userDb; 
	
	public LoginManager() throws ClassNotFoundException, SQLException {
		userDb = DatabaseFactory.createUserDatabase();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
