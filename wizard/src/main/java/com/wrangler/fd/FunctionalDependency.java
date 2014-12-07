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
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromAtt == null) ? 0 : fromAtt.hashCode());
		result = prime * result + ((toAtt == null) ? 0 : toAtt.hashCode());
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
		if (!(obj instanceof FunctionalDependency))
			return false;
		FunctionalDependency other = (FunctionalDependency) obj;
		if (fromAtt == null) {
			if (other.fromAtt != null)
				return false;
		} else if (!fromAtt.equals(other.fromAtt))
			return false;
		if (toAtt == null) {
			if (other.toAtt != null)
				return false;
		} else if (!toAtt.equals(other.toAtt))
			return false;
		return true;
	}

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
	
	public String toString() {
		return String.format("%s -> %s", fromAtt, toAtt);
	}
}
