package com.wrangler.normalization;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrangler.fd.FunctionalDependency;
import com.wrangler.load.Attribute;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;

public final class Normalizer {

	// Relation to be normalized
	private Relation rel;
	// Superkeys of the above relation, cached for 
	// repeated computation
	private Set<Attribute> superKeys;
	// Set of relations of this table broken into BCNF,
	// cached for repeated computation
	private Set<Relation> bcnf;
	// Set of relations for this table to be broken into 3nf,
	// cached for repeated computation
//	private Set<Relation> threeNF;

	private static final Logger LOG = LoggerFactory.getLogger(Normalizer.class);
	
	private Normalizer(Relation r) {
		this.rel = r;
	}
	
	/**
	 * Constructs new Normalizer instances for a particular relation.
	 * Because relation objects tend to cache particular results, using
	 * the same Normalizer object as much as possible should reduce cost
	 * of computation.
	 * 
	 * @param r
	 * @return
	 */
	public static Normalizer newInstance(Relation r) {
		return new Normalizer(r);
	}
	/**
	 * Given an Attribute a, and a set of Functional Dependencies s,
	 * returns all of the attributes that a determines
	 * 
	 * @param a
	 * @param s
	 * @return
	 */
	public Set<Attribute> findFdClosure(Attribute a, Set<FunctionalDependency> s) {
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
	 * Returns a set of all of the attributes a
	 * s.t. a -> r (i.e. the "superkeys" of r)
	 * 
	 * @param r
	 * @return
	 */
	public Set<Attribute> findSuperKeys() {
		// If we've already computed this result, then simply return that
		if(this.superKeys != null) {
			return this.superKeys;
		}
		// All superkeys of r
		Set<Attribute> sKeys = new HashSet<Attribute>();
		// All attrs of r
		Set<Attribute> tableAttrs = rel.getAttributes();
		// Check if each attr is a superkey of r
		for(Attribute a: tableAttrs) {
			if(isSuperKey(a)) {
				sKeys.add(a);
			}
		}
		this.superKeys = sKeys;
		return this.superKeys;
	}
	
	/**
	 * Returns true iff a given Attribute a is a superkey of the
	 * Relation r
	 * 
	 * @param a
	 * @param rel
	 * @return
	 */
	public boolean isSuperKey(Attribute a) {
		Set<Attribute> tableAttrs = rel.getAttributes();
		Set<FunctionalDependency> fds = rel.findAllHardFds();
		Set<Attribute> fdClosure = findFdClosure(a, fds);
		
		// Compare the closure of functional dependencies of a with
		// the attributes of r to see if a -> r
		return tableAttrs.equals(fdClosure);
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
	 * @return
	 */
	public Set<Relation> bcnf() {
		// If we've already computed this result then just return it
		if(bcnf != null) {
			return bcnf;
		}

		// The final result set of Relations s.t. each relation r
		// is in BCNF
		Set<Relation> normalized = new LinkedHashSet<Relation>();
		normalized.add(rel);
		Entry<Relation, FunctionalDependency> violating = findAndRemoveBcnfViolation(normalized);
		while(violating != null) {
			// Decompose on violating FD x -> y from r s.t. we create two relations:
			// {r - y} and {xy}
			Entry<Relation, Relation> decomposed = decomposeOn(violating.getKey(), violating.getValue());
			normalized.add(decomposed.getKey());
			normalized.add(decomposed.getValue());
			
			// Check to see if we still have any violating FDs
			violating = findAndRemoveBcnfViolation(normalized);
		}
		
		bcnf = normalized;
		return normalized;
	}
	
	/**
	 * Given a Relation r and a Functional Depdendency x -> y s.t. fd
	 * causes r to not be in BCNF, returns two tables:
	 * {r - y} and {xy}
	 * 
	 * @param rel
	 * @param fd
	 * @return
	 */
	private Entry<Relation, Relation> decomposeOn(Relation rel,
			FunctionalDependency fd) {
		// Initialize rMinusY table
		Set<Attribute> rMinusYAtts = new LinkedHashSet<Attribute>();
		rMinusYAtts.addAll(rel.getAttributes());
		rMinusYAtts.remove(fd.getToAtt());
		Relation rMinusY = RelationFactory.createNewRelation(rel.getName(), rMinusYAtts);

		// Initialize xy decomposed table
		Set<Attribute> xyAtts = new LinkedHashSet<Attribute>();
		xyAtts.add(fd.getFromAtt());
		xyAtts.add(fd.getToAtt());
		String xyName = fd.getFromAtt().getName() + "_" + fd.getToAtt().getName();
		Relation xy = RelationFactory.createNewRelation(xyName, xyAtts);
		
		return new SimpleEntry<Relation, Relation>(rMinusY, xy);
	}

	/**
	 * Removes and returns the relation r and Functional Dependency fd that are
	 * not in BCNF
	 * @param normalized
	 * @return
	 */
	private Entry<Relation, FunctionalDependency> findAndRemoveBcnfViolation(
			Set<Relation> normalized) {
		Entry<Relation, FunctionalDependency> violation = findBcnfViolation(normalized);
		normalized.remove(violation.getKey());
		return violation;
	}

	/**
	 * Returns a pair containg a Relation r and a Functional Dependency of r, fd,
	 * s.t. fd is not a superkey of r. Returns null if no such fds exist, i.e.
	 * if the table is already in Bcnf
	 * 
	 * @param normalized
	 * @return
	 */
	private Entry<Relation, FunctionalDependency> findBcnfViolation(Set<Relation> normalized) {
		for(Relation r: normalized) {
			FunctionalDependency violatingFd = findNonBcnfFd();
			if(violatingFd != null) {
				return new SimpleEntry<Relation, FunctionalDependency>(r, violatingFd);
			}
		}
		return null;
	}

	/**
	 * Returns a Functional Dependency fd of r iff fd is not
	 * a superkey of r
	 * 
	 * @return
	 */
	private FunctionalDependency findNonBcnfFd() {
		Set<FunctionalDependency> fds = rel.findAllHardFds();
		for(FunctionalDependency fd: fds) {
			// If any fd a -> b exists in r s.t. a is not a superkey
			// of r, then return false
			if(!isSuperKey(fd.getFromAtt())) {
				return fd;
			} 
		}
		return null;
	}
	
	/**
	 * USED ONLY FOR UNIT TESTING
	 */
	public static void main(String[] args) {
		
	}
}
