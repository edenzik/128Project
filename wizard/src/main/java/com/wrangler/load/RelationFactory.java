package com.wrangler.load;

import java.sql.SQLException;

/**
 * Factory for returning new Relation objects.
 * 
 * @author kahliloppenheimer
 *
 */
public class RelationFactory {
	
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
