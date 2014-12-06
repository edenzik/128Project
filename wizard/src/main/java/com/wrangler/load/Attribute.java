package com.wrangler.load;


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
	
	public Attribute(String name, PostgresAttType attType, Relation sourceTable) {
		this.name = name;
		this.attType = attType;
		this.sourceTable = sourceTable;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
//	@Override
//	public String toString() {
//		return String.format("Attribute [name=%s, sourceTable=%s, attType=%s]",
//				name, sourceTable, attType);
//	}

	@Override
	public String toString() {
		return String.format("%s.%s", getSourceTable().getName(), this.getName());
	}
}
