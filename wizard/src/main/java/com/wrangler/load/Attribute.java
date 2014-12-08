package com.wrangler.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrangler.constraint.ForeignKey;


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
	private boolean existing;
	
	private static final Logger LOG = LoggerFactory.getLogger(Attribute.class);

	private Attribute(String name, PostgresAttType attType, Relation sourceTable, boolean existing) {
		this.name = name;
		this.attType = attType;
		this.sourceTable = sourceTable;
		this.existing = existing;
	}

	/**
	 * Returns an Attribute object that represents an existing Attribute in a relation
	 * 
	 * @param name
	 * @param attType
	 * @param sourceTable
	 * @param constraints
	 * @return
	 */
	public static Attribute existingAttribute(String name, PostgresAttType attType, 
			Relation sourceTable){
		return new Attribute(name, attType, sourceTable, true);
	}
	
	/**
	 * Returns an Attribute object that represents a newly created Attribute not actually
	 * in a relation
	 * 
	 * @param name
	 * @param attType
	 * @return
	 */
	public static Attribute newAttribute(String name, PostgresAttType attType) {
		return new Attribute(name, attType, null, false);
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
	 * @return the existing
	 */
	public boolean exists() {
		return existing;
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
		
		ForeignKey fkpk = ForeignKey.newInstance(this, pk);
		getSourceTable().addFk(fkpk);
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