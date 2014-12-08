/**
 * 
 */
package com.wrangler.fd;

import java.util.Map;

import com.wrangler.load.Attribute;

/**
 * Represents a "soft" functional dependency (i.e. one in which
 * one or more values may contradict the functional dependency,
 * but are thought to be user-introduced errors)
 * 
 * @author kahliloppenheimer
 *
 */
public class SoftFD extends FunctionalDependency {

	/**
	 * 
	 */
	protected SoftFD(Attribute fromAtt, Attribute toAtt) {
		super(fromAtt, toAtt);
	}
	
	/**
	 * Returns all violations of this soft fd in the given Relation paired with
	 * the proportion of the result that they occupy. In other words, the following
	 * data associated with this soft fd, a -> b
	 * a 	->		b
	 * Brandeis, Waltham
	 * Brandeis, Waltham
	 * Brandeis, Waltam
	 * 
	 * would return {Waltham -> 66.6, Waltam -> 33.3}
	 * 
	 * @return
	 */
	public Map<String, Double> getViolations() {
		return getFromAtt().getSourceTable().getSourceDb().getDbHelper().getViolations(this);
	}

}
