package com.wrangler.load;

import java.util.Set;


/**
 * Factory for returning new Relation objects.
 * 
 * @author kahliloppenheimer
 *
 */
public final class RelationFactory {
	
	// Used to enforce non-instantiability
	private RelationFactory() {
		throw new AssertionError();
	}

	/**
	 * Factory method for returning new Relation object that already exists
	 * in the database.
	 * 
	 * @param name
	 * @param sourceDb
	 * @return
	 */
	public static Relation createExistingRelation(String name, Database sourceDb) {
		return new Relation(name, sourceDb);
	}
	
	/**
	 * Factory method for creating new Relation objects that do not already exist
	 * in any database.
	 * 
	 * @param name
	 * @param attrs
	 * @return
	 */
	public static Relation createNewRelation(String name, Set<Attribute> attrs) {
		return new Relation(name, attrs);
	}

}
