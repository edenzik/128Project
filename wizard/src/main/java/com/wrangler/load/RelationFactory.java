package com.wrangler.load;


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
	 * Factory method for returning new Relation object.
	 * 
	 * @param name
	 * @param sourceDb
	 * @return
	 */
	public static Relation createRelation(String name, Database sourceDb) {
		return new Relation(name, sourceDb);
	}

}
