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
	
	private static final String DEFAULT_IP = "104.236.17.70";
	private static final String DEFAULT_PORT = "5432";
	private static final String DEFAULT_USER = "kahlil";
	private static final String DEFAULT_PASS = "psswd";
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
	 * Assumes all default values. Deprecated because these values really should be passed and not
	 * assumed.
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Deprecated
	public Database() throws ClassNotFoundException, SQLException{
		this("default");
	}
	
	/**
	 * Assumes all default values except db name. Deprecated because the other values really should
	 * be passed and not assumed. 
	 * @param dbName
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Deprecated
	public Database(String dbName) throws ClassNotFoundException, SQLException{
		this(DEFAULT_IP, DEFAULT_PORT, dbName, DEFAULT_USER, DEFAULT_PASS);
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
