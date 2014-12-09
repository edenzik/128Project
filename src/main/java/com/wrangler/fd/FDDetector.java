package com.wrangler.fd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrangler.load.Attribute;
import com.wrangler.load.Database;
import com.wrangler.load.DatabaseFactory;
import com.wrangler.load.Host;
import com.wrangler.load.HostFactory;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
import com.wrangler.load.TableNotFoundException;

/**
 * Object to help detect functional dependencies in a given database
 * 
 * @author kahliloppenheimer
 *
 */
public class FDDetector {

	// The database to which this FDHelper is applied
	private static final Logger LOG = LoggerFactory.getLogger(FDDetector.class);

	/**
	 * USED TO ENFORCE NON-INSTANTIABILITY
	 * 
	 * @param db
	 */
	private FDDetector(Database db) {
		throw new AssertionError();
	}

	/**
	 * Returns a set of all hard functional dependencies in the given relation
	 * 
	 * @return set of all hard FDs
	 * @throws TableNotFoundException if passed table does not exist in db
	 * @throws SQLException 
	 */
	public static Set<FunctionalDependency> findAllHardFds(Relation rel)  {
		Set<FunctionalDependency> hardFdSet = new HashSet<FunctionalDependency>();
		Database db = rel.getSourceDb();
		// Check to see if table doesn't exist
		if(!db.getDbHelper().tableExists(rel)) {
			LOG.error("Attempted to find hard fds of non-existant table {}", rel.getName());
			throw new IllegalArgumentException("Attempted to find hard fds of non-existant table");
		}

		Set<Attribute> attrs;
		attrs = rel.getAttributes();

		// Check hard FDs for every possible pairing of atts
		for(Attribute fromAtt : attrs) {
			for(Attribute toAtt : attrs) {
				// Don't check FD from att to itself
				if(fromAtt != toAtt) {
					if(isHardFd(fromAtt, toAtt)) {
						FunctionalDependency fd = FDFactory.createHardFD(fromAtt, toAtt);
						hardFdSet.add(fd);
					}
				}
			}
		}
		return hardFdSet;
	}

	/**
	 * Returns true iff fromAtt is functionally dependent on toAtt
	 * 
	 * @param fromAtt
	 * @param toAtt
	 * @return
	 */
	public static boolean isHardFd(Attribute fromAtt, Attribute toAtt) {
		LOG.info("Checking {} -> {} as hard fd...", fromAtt, toAtt);
		try {
			if(getFdViolationCount(fromAtt, toAtt) == 0) {
				LOG.info("Confirmed {} -> {} as hard fd", fromAtt, toAtt);
				return true;
			}
			else {
				LOG.info("Denied {} -> {} as hard fd", fromAtt, toAtt);
				return false;
			}
		} catch (SQLException e) {
			LOG.error("", e);
			return false;
		}
	}

	/**
	 * Given two attributes, returns the number of violations of the potential
	 * FD between them. Returns -1 if error was encountered during calculation.
	 * 
	 * @param fromAtt
	 * @param toAtt
	 * @return
	 * @throws SQLException 
	 */
	private static int getFdViolationCount(Attribute fromAtt, Attribute toAtt) throws SQLException {
		// Make sure both attributes are from the same table
		if(!fromAtt.getSourceTable().equals(toAtt.getSourceTable())) {
			LOG.error("Trying to calculate hard FDs for relations from two separate relations: {} and {}",
					fromAtt.getSourceTable().getName(), toAtt.getSourceTable().getName());
			return -1;
		}

		String relName = fromAtt.getSourceTable().getName();
		String query = 
				String.format("SELECT COUNT(*) FROM (SELECT %s FROM (SELECT DISTINCT %s,%s FROM %s) AS temp GROUP BY %s HAVING count(%s)>1) as FDViolationCount;",
						fromAtt.getName(), fromAtt.getName(), toAtt.getName(), relName, fromAtt.getName(), toAtt.getName());
		ResultSet rs = fromAtt.getSourceTable().getSourceDb().getDbHelper().executeQuery(query);
		try {
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			LOG.error("", e);
		}
		LOG.error("Failed to generate FD violation count!");
		return -1;
	}


	/**
	 * Used for unit testing only
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Host host = HostFactory.createDefaultHost();
		try {
			Database db = DatabaseFactory.createDatabase("cosi128a", host);
			Relation rel = RelationFactory.createExistingRelation("cities_extended", db);
//			Set<FunctionalDependency> fds = rel.findAllHardFds();
//			System.out.println("fds = " + fds);
//			Normalizer n = Normalizer.newInstance(rel);
//			for(FunctionalDependency fd: fds) {
//				Attribute att = fd.getFromAtt();
//				Set<Attribute> closure = n.findFdClosure(att, fds);
//				System.out.printf("Closure for %s = %s\n", att, closure);
//			}
//			
			for(int i = 0; i < 100; ++i) {
				long start = System.currentTimeMillis();
				@SuppressWarnings("unused")
				Set<FunctionalDependency> fds = rel.findAllHardFds();
				long end = System.currentTimeMillis();
				System.out.printf("%d:\t%d\n", i, (end - start));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
