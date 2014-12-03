/**
 * 
 */
package com.wrangler.load;


/**
 * @author kahliloppenheimer
 *
 */
public class Host {
	
	private final String IP;
	private final String PORT;
	private final String ROLE;
	private final String PASS;
	
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
	protected Host(String hostIp, String port, String role, String pass) {
		this.IP = hostIp;
		this.PORT = port;
		this.ROLE = role;
		this.PASS = pass;
	}
	
	protected Host(String hostIp, String role, String pass) {
		this(hostIp, DEFAULT_PORT, role, pass);
	}
	
	/**
	 * Creates a host object with the default specifications
	 */
	protected Host() {
		this(DEFAULT_IP, DEFAULT_PORT, DEFAULT_ROLE, DEFAULT_PASS);
	}

	/**
	 * @return the hostIp
	 */
	public String getIp() {
		return IP;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return PORT;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return ROLE;
	}
	/**
	 * @return the pass
	 */
	public String getPass() {
		return PASS;
	}

}
