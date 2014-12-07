package com.wrangler.load;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory.getLogger(Relation.class);

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
		this.existing = false;
		Set<Attribute> copy = new LinkedHashSet<Attribute>();

		// Create new Attributes that actually refer to this table as their souce
		for(Attribute a: attrs) {
			copy.add(AttributeFactory.createAttribute(a.getName(), a.getAttType(), this));
		}
		this.attrs = copy;
	}
	/**
	 * Adds a functional dependency to relations that do not already exist in a db
	 * 
	 * @param fd
	 */
	public void addFd(FunctionalDependency fd) {
		if(exists()) {
			throw new AssertionError("Cannot add functional dependency to existing table!");
		}

		if(this.fds == null) {
			this.fds = new LinkedHashSet<FunctionalDependency>();
			this.fds.add(fd);
		} else {
			this.fds.add(fd);
		}

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
				return new LinkedHashSet<FunctionalDependency>();
			}
			this.fds = FDDetector.findAllHardFds(this);
		} 
		return this.fds;
	}

	/**
	 * Creates a relation representing this object in the sourceDb, then populates it with the
	 * relevant data from the passed source relation. This relation's attributes MUST be a subset
	 * of the passed relation's attributes so that the population can work correctly. The passed
	 * relation and db must, of course, be existing and populated. 
	 * 
	 * @param sourceRel
	 * @param sourceDb
	 * @return
	 */
	public boolean initializeAndPopulate(Relation sourceRel, Database sourceDb) {
		// Ensure nothing passed is null
		if(sourceRel == null || sourceDb == null) {
			throw new NullPointerException();
		}

		// If we're sure that everything else is correct, go ahead and try to initialize and populate the relation
		if(initialize(sourceDb)) {
			if(populate(sourceRel)) {
				return true;
			} else {
				LOG.warn("Created {} but could not populate with {} as source");
			}
		}
		return false;
	}
	/**
	 * Creates a table in the passed Database for this Relation object. Returns true if
	 * successful, false otherwise.
	 * 
	 * @param sourceDb
	 * @return
	 */
	private boolean initialize(Database sourceDb) {
		if(sourceDb.getDbHelper().createTable(this)) {
			this.sourceDb = sourceDb;
			this.existing = true;
			return true;
		}
		return false;
	}

	/**
	 * Given a source relation, populates this relation with the attributes it shares with
	 * the source relation.
	 * 
	 * @param sourceRel
	 * @return
	 */
	private boolean populate(Relation sourceRel) {
		// Validate passed source relation
		if(!sourceRel.exists()) {
			throw new AssertionError("Source relation must exist to be used as a source for populating another relation!");
		}
		if(sourceRel.getAttributes() == null) {
			throw new AssertionError("Source relation had no initialized attributes!");
		}
		// Make sure that this relation has subset of attributes from source relation
		if(!sourceRel.getAttributes().containsAll(getAttributes())) {
			throw new AssertionError(String.format("%s%s does not contain all attributes from %s%s", 
					sourceRel.getName(), sourceRel.getAttributes(), getName(), getAttributes()));
		}
		
		return getSourceDb().getDbHelper().populateTable(this, sourceRel);
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
