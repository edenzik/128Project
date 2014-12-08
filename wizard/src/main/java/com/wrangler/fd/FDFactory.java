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
		if(!fromAtt.exists() || !toAtt.exists()) {
			throw new AssertionError("Soft FDs on non-existing attributes not supported!");
		}
		if(!fromAtt.getSourceTable().exists() || !toAtt.getSourceTable().exists()) {
			throw new AssertionError("Soft FDs on non-existing tables not supported!");
		}
		return new SoftFD(fromAtt, toAtt);
	}

}
