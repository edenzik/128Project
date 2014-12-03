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
public class AttributeFactory {
	
	/**
	 * Factory method to return a new Attribute object.
	 * 
	 * @param name
	 * @param attType
	 * @param sourceTable
	 * @return
	 */
	public static Attribute createAttribute(String name, String attType, Relation sourceTable) {
		return new Attribute(name, attType, sourceTable);
	}

}
