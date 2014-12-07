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
	// Represents this table's primary key
	// (currently only one is supported)
	private Attribute primaryKey;

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
			copy.add(Attribute.withConstraints(a.getName(), a.getAttType(), this, a.getConstraints()));
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
	 * Decomposes the current Relation into the set of passed Relations
	 * (almost always the result of normalization algorithm like Normalizer::bcnf())
	 * 
	 * @param rels
	 * @return
	 */
	public boolean decomposeInto(Set<Relation> rels) {
		boolean failed = false;
		// Keep track of all of the relations that completed so that if any
		// failed we can undo the ones that did complete
		Set<Relation> succesfullyCompleted = new LinkedHashSet<Relation>();
		for(Relation r: rels) {
			if(this.equals(r)) {
				// If original table still exists, it means no decomposition ocurred
				// and we can simply return
				return true;
			}
			if(!r.initializeAndPopulate(this, getSourceDb())) {
				failed = true;
				break;
			}
		}
		// Delete any residue (normalized tables) if not all finished successfully
		if(failed) {
			LOG.error("Failed to initialize decomposed tables {}; deleting any that successfully completed...", rels);
			for(Relation r: succesfullyCompleted) {
				r.delete();
			}
			return false;
		}
		// Set up constraints
		for(Relation r: rels) {
			// Add primary key if it exists
			if(r.getPrimaryKey() != null) {
				getSourceDb().getDbHelper().addPrimaryKey(r.getPrimaryKey(), r);
			}
			Set<Attribute> attrs = r.getAttributes();
			// Set up attribute specific constraints (i.e. fk or unique)
			for(Attribute a: attrs) {
				Set<Constraint> constraints = a.getConstraints();
				for(Constraint c : constraints) {
					getSourceDb().getDbHelper().addConstraint(r, a, c);
				}
			}
		}
		
		return true;
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
	private boolean initializeAndPopulate(Relation sourceRel, Database sourceDb) {
		// Ensure nothing passed is null
		if(sourceRel == null || sourceDb == null) {
			throw new NullPointerException();
		}

		// If we're sure that everything else is correct, go ahead and try to initialize and populate the relation
		if(initialize(sourceDb)) {
			if(populate(sourceRel)) {
				return true;
			} else {
				LOG.warn("Created {} but could not populate with {} as source", this, sourceDb);
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
			LOG.info("Successfully initialized {}", this);
			return true;
		}
		LOG.warn("Failed to initialize {}", this);
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
	 * Deletes the given relation object from the database
	 */
	private void delete() {
		getSourceDb().getDbHelper().deleteTable(this);
		this.existing = false;
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
			findAndSetConstraints();
		}
		return this.attrs;
	}

	/**
	 * Finds and sets all constraints (i.e. fks and pks) for this relation
	 */
	private void findAndSetConstraints() {
		getSourceDb().getDbHelper().findAndSetConstraints(this);
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
			else {
				this.fds = FDDetector.findAllHardFds(this);
			}
		} 
		return this.fds;
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

	/**
	 * @return the primaryKey
	 */
	public Attribute getPrimaryKey() {
		return primaryKey;
	}
	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(Attribute primaryKey) {
		if(this.primaryKey != null) {
			LOG.warn("Replacing pk {} with {}", this.primaryKey, primaryKey);
		}
		this.primaryKey = primaryKey;
	}

}
