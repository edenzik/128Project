package com.wrangler.login;

import com.wrangler.load.Database;

public class UserFactory {
	
	/**
	 * Returns a user object associated with the given name, pass, and db
	 * 
	 * @param userName
	 * @param userPass
	 * @param db
	 * @return
	 */
	public static User createUser(String userName, String userPass, Database db) {
		return new User(userName, userPass, db);
	}

}
