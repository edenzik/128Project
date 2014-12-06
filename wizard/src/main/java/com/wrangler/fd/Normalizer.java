package com.wrangler.fd;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.wrangler.load.Attribute;

public final class Normalizer {

	// To enforce non-instantiability
	private Normalizer() {
		throw new AssertionError();
	}

	/**
	 * Given an Attribute a, and a set of Functional Dependencies s,
	 * returns all of the attributes that a determines
	 * 
	 * @param a
	 * @param s
	 * @return
	 */
	public static Set<Attribute> findFdClosure(Attribute a, Set<FunctionalDependency> s) {
		// Closure of attributes determined by a
		Set<Attribute> closure = new HashSet<Attribute>();
		// Stack of FDs left to compute closure of algorithm
		Stack<Attribute> attsToCompute = new Stack<Attribute>();
		attsToCompute.push(a);
		closure.add(a);

		while(!attsToCompute.isEmpty()) {
			Attribute curr = attsToCompute.pop();
			// For each att to analyze, go through the set of FDs
			// and add to the closure/queue up any attributes it
			// determines that we have not seen before
			for(FunctionalDependency fd: s) {
				Attribute from = fd.getFromAtt();
				Attribute to = fd.getToAtt();

				// If we encounter a matching fd and we have not seen
				// the "to" att, i.e. from -> to, then queue it up
				// and add it to our closure
				if(curr.equals(from) && !closure.contains(to)) {
					attsToCompute.push(to);
					closure.add(to);
				}
			}
		}
		return closure;
	}
}
