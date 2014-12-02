/**
 * 
 */
package com.wrangler.login;

import java.sql.SQLException;
import com.wrangler.load.Database;

/**
 * @author edenzik
 *
 */
public class User {
	private final String USER_NAME;
	private final String USER_PASSWORD;
	private final String USER_DB_NAME;
	private final Database db;
		
	public User(String user_name, String user_password) throws ClassNotFoundException, SQLException{
		this.USER_NAME = user_name;
		this.USER_PASSWORD = user_password;
		this.USER_DB_NAME = USER_NAME.split("@")[0];
		this.db = new Database(USER_DB_NAME);
		if (!db.getDbHelper().userExists(USER_NAME)){
			db.getDbHelper().addUser(user_name, user_password);
		}
	}
	
	public String getName(){return USER_NAME;}
	public String getPassword(){return USER_PASSWORD;}
	public Database getDB(){return db;}

}
