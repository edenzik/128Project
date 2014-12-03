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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IP == null) ? 0 : IP.hashCode());
		result = prime * result + ((PASS == null) ? 0 : PASS.hashCode());
		result = prime * result + ((PORT == null) ? 0 : PORT.hashCode());
		result = prime * result + ((ROLE == null) ? 0 : ROLE.hashCode());
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
		if (!(obj instanceof Host))
			return false;
		Host other = (Host) obj;
		if (IP == null) {
			if (other.IP != null)
				return false;
		} else if (!IP.equals(other.IP))
			return false;
		if (PASS == null) {
			if (other.PASS != null)
				return false;
		} else if (!PASS.equals(other.PASS))
			return false;
		if (PORT == null) {
			if (other.PORT != null)
				return false;
		} else if (!PORT.equals(other.PORT))
			return false;
		if (ROLE == null) {
			if (other.ROLE != null)
				return false;
		} else if (!ROLE.equals(other.ROLE))
			return false;
		return true;
	}

}
