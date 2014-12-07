package com.wrangler.load;

public abstract class Constraint {

	/**
	 * Returns a new primary key constraint with the the passed primary key
	 * 
	 * @param pk
	 * @return
	 */
	public static PrimaryKey newPrimaryKey(Attribute pk) {
		if(pk == null) {
			throw new IllegalArgumentException("Null pk passed!");
		}
		if(pk.getSourceTable() == null) {
			throw new IllegalArgumentException("pk belongs to no source table");
		}
		return new PrimaryKey(pk);
	}

	/**
	 * Returns a new foreign key constraint with fk as the foreign key of the 
	 * "referencing" relation and pk as the primary key of the "referenced" relation
	 * 
	 * @param fk
	 * @param pk
	 * @return
	 */
	public static ForeignKey newForeignKey(Attribute fk, Attribute pk) {
		if(fk == null || pk == null) {
			throw new IllegalArgumentException("Null fk or pk passed!");
		}
		if(fk.getSourceTable() == null || pk.getSourceTable() == null) {
			throw new IllegalArgumentException("fk or pk source table is null!");
		}
		return new ForeignKey(fk, pk);
	}
	
	/**
	 * Returns the constraint in SQL friendly format s.t. the result can be inlined
	 * into a create table statement if needed
	 * 
	 * @return
	 */
	public abstract String asSql();
	
	@Override
	public String toString() {
		return asSql();
	}
}

final class PrimaryKey extends Constraint {
	private final Attribute pk;
	
	protected PrimaryKey(Attribute pk) {
		this.pk = pk;
	}

	@Override
	public String asSql() {
		return "PRIMARY KEY";
	}

	/**
	 * @return the pk
	 */
	public Attribute getPk() {
		return pk;
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

/**
 * Represents a foreign key constraint between two attributes fk and pk
 * s.t. fk is in the "referencing" relation and "pk" is the primary key
 * of the "referenced" relation
 * 
 * @author kahliloppenheimer
 *
 */
final class ForeignKey extends Constraint {
	private final Attribute fk;
	private final Attribute pk;

	protected ForeignKey(Attribute fk, Attribute pk) {
		this.fk = fk;
		this.pk = pk;
		// Make sure that the fk keeps track of the constraint
		fk.addConstraint(this);
	}

	@Override
	public String asSql() {
		return String.format("REFERENCES %s", pk.getSourceTable().getName(), pk.getName());
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
