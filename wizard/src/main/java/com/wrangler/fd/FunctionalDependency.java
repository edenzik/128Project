package com.wrangler.fd;

import com.wrangler.load.Attribute;

/**
 * Represents a single functional dependency from one Attribute
 * to another
 * 
 * @author kahliloppenheimer
 *
 */
public abstract class FunctionalDependency {
	private final Attribute fromAtt;
	private final Attribute toAtt;

	protected FunctionalDependency(Attribute fromAtt, Attribute toAtt) {
		this.fromAtt = fromAtt;
		this.toAtt = toAtt;
	}

	/**
	 * @return the fromAtt
	 */
	public Attribute getFromAtt() {
		return fromAtt;
	}

	/**
	 * @return the toAtt
	 */
	public Attribute getToAtt() {
		return toAtt;
	}

}
