/**
 * 
 */
package com.wrangler.load;

/**
 * Factory to build attribute objects.
 * 
 * @author kahliloppenheimer
 *
 */
public final class AttributeFactory {
	
	// Used to enforce non-instantiability
	private AttributeFactory() {
		throw new AssertionError();
	}
	/**
	 * Factory method to return a new Attribute object.
	 * 
	 * @param name
	 * @param attType
	 * @param sourceTable
	 * @return
	 */
	public static Attribute createAttribute(String name, PostgresAttType attType, Relation sourceTable) {
		return new Attribute(name, attType, sourceTable);
	}

}
