/**
 * 
 */
package com.wrangler.login;

import java.sql.SQLException;

/**
 * @author edenzik
 *
 */
public class User {
	private final String USER_NAME;
	private final String USER_PASSWORD;
	private final String USER_DB_NAME;
	private final Database DB;
		
	public User(String user_name, String user_password) throws ClassNotFoundException, SQLException{
		this.USER_NAME = user_name;
		this.USER_PASSWORD = user_password;
		this.USER_DB_NAME = USER_NAME.split("@")[0];
		if (!Database.userExists(USER_NAME)){
			Database.addUser(user_name, user_password);
		}
		DB = new Database(USER_DB_NAME);
	}
	
	public String getName(){return USER_NAME;}
	public String getPassword(){return USER_PASSWORD;}
	public Database getDB(){return DB;}

}
