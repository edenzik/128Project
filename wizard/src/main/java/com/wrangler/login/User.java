/**
 * 
 */
package com.wrangler.login;

import com.wrangler.load.Database;

/**
 * @author edenzik
 *
 */
public class User {
	private final String name;
	private final String pass;
	private final Database db;
		
	/**
	 * @param name
	 * @param pass
	 * @param db
	 */
	protected User(String name, String pass, Database db) {
		this.name = name;
		this.pass = pass;
		this.db = db;
	}
	
	public String getName(){return name;}
	public String getPassword(){return pass;}
	public Database getDB(){return db;}

}
