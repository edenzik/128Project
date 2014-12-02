/**
 * 
 */
package com.wrangler.fd;

/**
 * Represents a "hard" functional dependency between two attributes
 * (i.e. one in which no values contradict the FD)
 * 
 * @author kahliloppenheimer
 *
 */
public class HardFD extends FunctionalDependency {

	/**
	 * @param fromAtt
	 * @param toAtt
	 */
	public HardFD(Attribute fromAtt, Attribute toAtt) {
		super(fromAtt, toAtt);
	}

}
