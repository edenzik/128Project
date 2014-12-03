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
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	protected Database(String dbName, Host host) throws ClassNotFoundException, SQLException {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Database))
			return false;
		Database other = (Database) obj;
		if (dbName == null) {
			if (other.dbName != null)
				return false;
		} else if (!dbName.equals(other.dbName))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		return true;
	}
	

}
