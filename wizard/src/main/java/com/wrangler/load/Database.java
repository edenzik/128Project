package com.wrangler.load;

import java.sql.SQLException;

/**
 * Represents an instance of a database.
 * 
 * @author kahliloppenheimer
 *
 */
public class Database {
	
	// The name of this database
	private final String dbName;
	// The host of this db
	private final Host host;
	// Facilitates any actual interaction with the database
	private final DBHelper dbHelper;

	
	/**
	 * @param dbName
	 * @param host
	 * @param dbHelper
	 */
	protected Database(String dbName, Host host) {
		this.dbName = dbName;
		this.host = host;
		this.dbHelper = new DBHelper(this);
	}

	/**
	 * Creates the given database if it doesn't already exist. Returns true if the db
	 * was created, and false if it already existed.
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	boolean initialize() throws ClassNotFoundException, SQLException{
		if (!getDbHelper().databaseExists(getDbName())) {
			return getDbHelper().createDatabase(getDbName());
		}
		return false;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}
	/**
	 * @return the dbHelper
	 */
	public DBHelper getDbHelper() {
		return dbHelper;
	}

	/**
	 * @return the host
	 */
	public Host getHost() {
		return host;
	}
	

}
