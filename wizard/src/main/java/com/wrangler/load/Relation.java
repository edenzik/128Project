package com.wrangler.load;

import java.sql.SQLException;
import java.util.Set;

import com.wrangler.fd.FDDetector;
import com.wrangler.fd.FunctionalDependency;

/**
 * Represents a single relation within a database.
 * 
 * @author kahliloppenheimer
 *
 */
public class Relation {
	private final String name;
	private final Database sourceDb;
	private Set<FunctionalDependency> fds;
	/**
	 * @param name
	 * @param attributes
	 * @param sourceDb
	 * @throws SQLException 
	 */
	protected Relation(String name, Database sourceDb) {
		this.name = name;
		this.sourceDb = sourceDb;
		// Null until client actually queries for FDs
		this.fds = null;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return dbName.relationName
	 */
	public String getFullyQualifiedName() {
		return String.format("%s.%s", getSourceDb(), getName());
	}
	/**
	 * @return the attributes
	 * @throws SQLException 
	 */
	public Set<Attribute> getAttributes() {
		return sourceDb.getDbHelper().getRelationAttributes(this);
	}

	/**
	 * @return the sourceDb
	 */
	public Database getSourceDb() {
		return sourceDb;
	}
	/**
	 * Returns the set of functional dependencies (FDs) for this Relation.
	 * The FDs are cached after the first access, so subsequent accesses
	 * are not expensive.
	 * 
	 * @return the fds
	 */
	public Set<FunctionalDependency> findAllHardFds() {
		if(fds == null) {
			fds = FDDetector.findAllHardFds(this);
		} 
		return fds;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
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
