/**
 * 
 */
package com.wrangler.fd;

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

}
