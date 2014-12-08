package com.wrangler.constraint;

import com.wrangler.load.Attribute;

public class PrimaryKey {
	private final Attribute pk;
	
	private PrimaryKey(Attribute pk) {
		this.pk = pk;
	}
	
	public static PrimaryKey newInstance(Attribute pk) {
		return new PrimaryKey(pk);
	}

	/**
	 * Returns a representaiton of this constraint for insertion into a create table
	 * statement.
	 * 
	 * @return
	 */
	public String asSql() {
		return "PRIMARY KEY";
	}

	/**
	 * @return the pk
	 */
	public Attribute getPk() {
		return pk;
	}

	@Override
	public String toString() {
		return this.pk + " " + asSql();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pk == null) ? 0 : pk.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PrimaryKey)) {
			return false;
		}
		PrimaryKey other = (PrimaryKey) obj;
		if (pk == null) {
			if (other.pk != null) {
				return false;
			}
		} else if (!pk.equals(other.pk)) {
			return false;
		}
		return true;
	}
}