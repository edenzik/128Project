package com.wrangler.fd;

import com.wrangler.load.Attribute;

/**
 * Factory for producing Functional Dependencies
 * 
 * @author kahliloppenheimer
 *
 */
public final class FDFactory {
	
	// Used to enforce non-instantiability
	private FDFactory() {
		throw new AssertionError();
	}
	
	public static FunctionalDependency createHardFD(Attribute fromAtt, Attribute toAtt) {
		return new HardFD(fromAtt, toAtt);
	}
	
	public static FunctionalDependency createSoftFD(Attribute fromAtt, Attribute toAtt) {
		return new SoftFD(fromAtt, toAtt);
	}

}
