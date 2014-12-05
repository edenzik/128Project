/**
 * 
 */
package com.wrangler.load;

import java.sql.SQLException;

/**
 * @author kahliloppenheimer
 *
 */
public final class DatabaseFactory {
	
	// Used to enforce non-instantiablility
	private DatabaseFactory() {
		throw new AssertionError();
	}
	/**
	 * Returns a new database for this host with the given dbName
	 * 
	 * TODO: Add instance control to make sure only one instance
	 * of Database object for any given Database exists.
	 * 
	 * @param dbName
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static Database createDatabase(String dbName, Host host) throws ClassNotFoundException, SQLException {
		return new Database(dbName, host);
	}
	
	public static UserDatabase createUserDatabase() throws ClassNotFoundException, SQLException {
		return new UserDatabase();
	}
	
	protected static Database defaultDatabase(String dbName) throws ClassNotFoundException, SQLException {
		return new Database(dbName, HostFactory.createDefaultHost());
	}

}
