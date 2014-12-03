package com.wrangler.load;

import java.sql.SQLException;
import java.util.Set;

/**
 * Represents a single relation within a database.
 * 
 * @author kahliloppenheimer
 *
 */
public class Relation {
	private final String name;
	private final Set<Attribute> attributes;
	private final Database sourceDb;
	/**
	 * @param name
	 * @param attributes
	 * @param sourceDb
	 * @throws SQLException 
	 */
	protected Relation(String name, Database sourceDb) throws SQLException {
		this.name = name;
		this.sourceDb = sourceDb;
		this.attributes = sourceDb.getDbHelper().getTableAttributes(this);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the attributes
	 */
	public Set<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return the sourceDb
	 */
	public Database getSourceDb() {
		return sourceDb;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((sourceDb == null) ? 0 : sourceDb.hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		Relation other = (Relation) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sourceDb == null) {
			if (other.sourceDb != null)
				return false;
		} else if (!sourceDb.equals(other.sourceDb))
			return false;
		return true;
	}
	
}
