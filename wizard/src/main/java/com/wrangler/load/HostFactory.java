package com.wrangler.load;

public class HostFactory {
	
	/**
	 * Creates a host with the passed parameters
	 * 
	 * @param hostIp
	 * @param port
	 * @param role
	 * @param pass
	 * @return
	 */
	public static Host createHost(String hostIp, String port, String role, String pass) {
		return new Host(hostIp, port, role, pass);
	}
	
	/**
	 * Returns a host with the default parameters specified in Host
	 * 
	 * @return
	 */
	public static Host createDefaultHost() {
		return new Host();
	}

}
