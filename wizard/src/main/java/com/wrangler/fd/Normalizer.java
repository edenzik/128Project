package com.wrangler.fd;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import com.wrangler.load.Attribute;
import com.wrangler.load.Relation;

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

	/**
	 * Returns true iff a given Attribute a is a superkey of the
	 * Relation r
	 * 
	 * @param a
	 * @param r
	 * @return
	 */
	public static boolean isSuperKey(Attribute a, Relation r) {
		Set<Attribute> tableAttrs = r.getAttributes();
		Set<FunctionalDependency> fds = r.findAllHardFds();
		Set<Attribute> fdClosure = findFdClosure(a, fds);
		
		// Compare the closure of functional dependencies of a with
		// the attributes of r to see if a -> r
		return tableAttrs.equals(fdClosure);
	}
	
	/**
	 * Given a relation r, returns a set of all of the attributes a
	 * s.t. a -> r (i.e. the "superkeys" of r)
	 * 
	 * @param r
	 * @return
	 */
	public static Set<Attribute> findSuperKeys(Relation r) {
		// All superkeys of r
		Set<Attribute> superKeys = new HashSet<Attribute>();
		// All attrs of r
		Set<Attribute> tableAttrs = r.getAttributes();
		// Check if each attr is a superkey of r
		for(Attribute a: tableAttrs) {
			if(isSuperKey(a, r)) {
				superKeys.add(a);
			}
		}
		return superKeys;
	}
	
	/**
	 * Given a relation r, returns the set of relations S s.t. that
	 * each relation s in S is in BCNF.
	 * 
	 * For relation R to be in BCNF, all the functional dependencies (FDs) 
	 * that hold in R need to satisfy property that the determinants X are 
	 * all superkeys of R. i.e. if X->Y holds in R, then X must be a superkey
	 * of R to be in BCNF.
	 * 
	 * @param r
	 * @return
	 */
	public static Set<Relation> bcnf(Relation r) {
		Set<Relation> normalized = new LinkedHashSet<Relation>();

		return normalized;
	}
}
