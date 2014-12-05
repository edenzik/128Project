package com.wrangler.load;

import java.sql.SQLException;

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
	 * @throws SQLException
	 */
	public static Relation createRelation(String name, Database sourceDb) throws SQLException {
		return new Relation(name, sourceDb);
	}

}
