package com.wrangler.constraint;

import com.wrangler.load.Attribute;

/**
 * Represents a foreign key constraint between two attributes fk and pk
 * s.t. fk is in the "referencing" relation and "pk" is the primary key
 * of the "referenced" relation
 * 
 * @author kahliloppenheimer
 *
 */
public class ForeignKey {
	private final Attribute fk;
	private final Attribute pk;

	private ForeignKey(Attribute fk, Attribute pk) {
		this.fk = fk;
		this.pk = pk;
	}
	
	public static ForeignKey newInstance(Attribute fk, Attribute pk) {
		return new ForeignKey(fk, pk);
	}

	/**
	 * Returns a string representation of this foreign key constraint fit to
	 * be put right into a create table statement
	 * 
	 * @return
	 */
	public String asSql() {
		return String.format("REFERENCES %s", pk.getSourceTable().getName());
	}

	/**
	 * @return the fk
	 */
	public Attribute getFk() {
		return fk;
	}

	/**
	 * @return the pk
	 */
	public Attribute getPk() {
		return pk;
	}

	@Override
	public String toString() {
		return getPk() + " " + asSql();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fk == null) ? 0 : fk.hashCode());
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
		if (!(obj instanceof ForeignKey)) {
			return false;
		}
		ForeignKey other = (ForeignKey) obj;
		if (fk == null) {
			if (other.fk != null) {
				return false;
			}
		} else if (!fk.equals(other.fk)) {
			return false;
		}
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