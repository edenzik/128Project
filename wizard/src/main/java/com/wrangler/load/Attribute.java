package com.wrangler.load;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a single attribute within a table
 * 
 * @author kahliloppenheimer
 *
 */
public class Attribute {

	private final String name;
	private final Relation sourceTable;
	private final PostgresAttType attType;
	private Set<Constraint> constraints;
	
	private static final Logger LOG = LoggerFactory.getLogger(Attribute.class);

	private Attribute(String name, PostgresAttType attType, Relation sourceTable, Set<Constraint> constraints) {
		this.name = name;
		this.attType = attType;
		this.sourceTable = sourceTable;
		this.setConstraints(constraints);
	}

	/**
	 * Returns a new Attribute without any constraints (i.e. primary key or foreign key)
	 * 
	 * @param name
	 * @param attType
	 * @param sourceTable
	 * @return
	 */
	public static Attribute withoutConstraints(String name, PostgresAttType attType, Relation sourceTable) {
		return new Attribute(name, attType, sourceTable, new HashSet<Constraint>());
	}

	/**
	 * Returns a new Attribute with specified constraints (i.e. primary key or foreign key)
	 * 
	 * @param name
	 * @param attType
	 * @param sourceTable
	 * @param constraints
	 * @return
	 */
	public static Attribute withConstraints(String name, PostgresAttType attType, 
			Relation sourceTable, Set<Constraint> constraints) {
		return new Attribute(name, attType, sourceTable, constraints);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the attType
	 */
	public PostgresAttType getAttType() {
		return attType;
	}

	/**
	 * @return the sourceTable
	 */
	public Relation getSourceTable() {
		return sourceTable;
	}

	/**
	 * @return the constraints
	 */
	public Set<Constraint> getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints the constraints to set
	 */
	public void setConstraints(Set<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	/**
	 * Adds the passed constraint to this attributes maintained set of
	 * constraints. Creates the set if it did not exist before.
	 * 
	 * @param c
	 */
	public void addConstraint(Constraint c) {
		if(this.constraints == null) {
			Set<Constraint> cSet = new HashSet<Constraint>();
			cSet.add(c);
			setConstraints(cSet);
		} else {
			this.constraints.add(c);
		}
		System.out.println("Now constraints = " + constraints + " for " + this.getSourceTable() + "." + this.getName());
	}
	
	/**
	 * Adds a new foreign key constraint to this attribute to reference the passed
	 * primary key
	 * 
	 * @param pk
	 */
	public void addFK(Attribute pk) {
		if(pk == null || pk.getSourceTable() == null) {
			throw new IllegalArgumentException("Invalid pk: " + pk);
		}
		
		Constraint fkpk = Constraint.newForeignKey(this, pk);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attType == null) ? 0 : attType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Attribute other = (Attribute) obj;
		if (attType != other.attType)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return getName();
	}

}