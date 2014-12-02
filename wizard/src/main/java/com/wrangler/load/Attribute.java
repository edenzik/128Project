package com.wrangler.load;

import com.wrangler.load.PostgresAttType;

public class Attribute {
	
	private final String name;
	private final PostgresAttType attType;
	
	public Attribute(String name, PostgresAttType attType) {
		this.name = name;
		this.attType = attType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the attType
	 */
	public PostgresAttType getAttType() {
		return attType;
	}

}
