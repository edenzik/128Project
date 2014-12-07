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
	// Name of this relation
	private final String name;
	// Source db to which this relation belongs
	private Database sourceDb;
	// True iff this Relation exists in a database already
	private boolean existing;
	// Set of attributes for this relation
	// (calculated the first time it's requested)
	private Set<Attribute> attrs = null;
	// Set of functional dependencies for this relation
	// (calculated the first time it's requested)
	private Set<FunctionalDependency> fds = null;


	/**
	 * Constructor for a Relation object that maps to a Relation which
	 * exists in a database already
	 * 
	 * @param name
	 * @param attributes
	 * @param sourceDb
	 * @throws SQLException 
	 */
	protected Relation(String name, Database sourceDb) {
		this.name = name;
		this.sourceDb = sourceDb;
		this.existing = true;

	}
	/**
	 * Constructor for a Relation object that maps to a newly created Relation
	 * object.
	 * 
	 * @param name
	 * @param attrs
	 */
	protected Relation(String name, Set<Attribute> attrs) {
		this.name = name;
		this.attrs = attrs;
		this.existing = false;
	}
	/**
	 * Returns the set of functional dependencies (FDs) for this Relation.
	 * The FDs are cached after the first access, so subsequent accesses
	 * are not expensive.
	 * 
	 * @return the fds
	 */
	public Set<FunctionalDependency> findAllHardFds() {
		if(this.fds == null) {
			if(!exists()) {
				throw new AssertionError("Cannot find FDs for non-existant relation!");
			}
			this.fds = FDDetector.findAllHardFds(this);
		} 
		return this.fds;
	}
	
	/**
	 * Creates a table in the passed Database for this Relation object. Returns true if
	 * successful, false otherwise.
	 * 
	 * @param sourceDb
	 * @return
	 */
	public boolean initialize(Database sourceDb) {
		if(sourceDb.getDbHelper().createTable(this)) {
			this.existing = true;
			return true;
		}
		return false;
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
		if(getSourceDb() == null) {
			return getName();
		} else {
			return String.format("%s.%s", getSourceDb(), getName());
		}
	}
	/**
	 * Returns the attributes of a given Relation. The initial call to this function
	 * performs a lookup in the database, but the results are cached so that later
	 * calls are less expensive.
	 * 
	 * @return the attributes
	 * @throws SQLException 
	 */
	public Set<Attribute> getAttributes() {
		if(this.attrs == null) {
			if(!exists()) {
				throw new AssertionError("Database can't both be non-existant and not have attributes!");
			}
			this.attrs = sourceDb.getDbHelper().getRelationAttributes(this);
		}
		return this.attrs;
	}
	/**
	 * @return the sourceDb
	 */
	public Database getSourceDb() {
		return sourceDb;
	}
	/**
	 * Returns true iff this relation exists in a database
	 * 
	 * @return
	 */
	public boolean exists() {
		return existing;
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
