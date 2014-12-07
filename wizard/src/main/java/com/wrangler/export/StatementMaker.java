/**
 * 
 */
package com.wrangler.export;

import com.wrangler.load.Relation;

/**
 * @author edenzik
 *
 */
abstract class StatementMaker {
	protected final StringBuilder statement;

	/**
	 * Create a Table Definition from a relation
	 * 
	 */
	StatementMaker(Relation rel) {
		statement = new StringBuilder();
	}
	
	public String getStatement(){
		return statement.toString();
	}
	
	

}
