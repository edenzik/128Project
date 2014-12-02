package com.wrangler.load;

import java.sql.SQLException;

/**
 * Represents an instance of a database.
 * 
 * @author kahliloppenheimer
 *
 */
public class Database {
	
	private final String hostIp;
	private final String port;
	private final String dbName;
	private final String user;
	private final String pass;
	private final DBHelper dbHelper;
	
	private static final String DEFAULT_PORT = "5432";
	/**
	 * @param hostIp
	 * @param port
	 * @param dbName
	 * @param user
	 * @param pass
	 * @throws SQLException if DB can't be connected to
	 * @throws ClassNotFoundException if postgreSQL driver can't be loaded
	 */
	public Database(String hostIp, String port, String dbName, String user,
			String pass) throws ClassNotFoundException, SQLException {
		this.hostIp = hostIp;
		this.port = port;
		this.dbName = dbName;
		this.user = user;
		this.pass = pass;
		dbHelper = new DBHelper(this);
	}
	
	/**
	 * Assumes default port
	 * 
	 * @param hostIp
	 * @param dbName
	 * @param user
	 * @param pass
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public Database(String hostIp, String dbName, String user, String pass) throws ClassNotFoundException, SQLException {
		this(hostIp, DEFAULT_PORT, dbName, user, pass);
	}
	/**
	 * @return the hostIp
	 */
	public String getHostIp() {
		return hostIp;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @return the dbHelper
	 */
	public DBHelper getDbHelper() {
		return dbHelper;
	}
	

}
