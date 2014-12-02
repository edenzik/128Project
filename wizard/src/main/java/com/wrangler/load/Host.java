/**
 * 
 */
package com.wrangler.load;

/**
 * @author kahliloppenheimer
 *
 */
public class Host {
	
	private final String ip;
	private final String port;
	private final String role;
	private final String pass;
	
	private static final String DEFAULT_IP = "104.236.17.70";
	private static final String DEFAULT_PORT = "5432";
	private static final String DEFAULT_ROLE = "kahlil";
	private static final String DEFAULT_PASS = "psswd";
	/**
	 * @param hostIp
	 * @param port
	 * @param role
	 * @param pass
	 */
	public Host(String hostIp, String port, String role, String pass) {
		this.ip = hostIp;
		this.port = port;
		this.role = role;
		this.pass = pass;
	}
	
	/**
	 * Creates a host object with the default specifications
	 */
	public Host() {
		this(DEFAULT_IP, DEFAULT_PORT, DEFAULT_ROLE, DEFAULT_PASS);
	}
	/**
	 * Returns a new database for this host with the given dbName
	 * 
	 * TODO: Add instance control to make sure only one instance
	 * of Database object for any given Database exists.
	 * 
	 * @param dbName
	 * @return
	 */
	public Database createDatabase(String dbName) {
		return new Database(dbName, this);
	}
	/**
	 * @return the hostIp
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

}
