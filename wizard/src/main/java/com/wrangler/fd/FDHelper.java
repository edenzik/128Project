package com.wrangler.fd;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrangler.load.DBHelper;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;
import com.wrangler.load.TableNotFoundException;

/**
 * Object to help detect and analyze functional dependencies in a given database
 * 
 * @author kahliloppenheimer
 *
 */
public class FDHelper {
	
	// The database to which this FDHelper is applied
	private final Database db;
	// A helper object for actually interacting with the DB
	private DBHelper dbHelper;
	private final Logger LOG = LoggerFactory.getLogger(FDHelper.class);
	
	public FDHelper(Database db) {
		this.db = db;
		try {
			this.dbHelper = new DBHelper(db);
		} catch (ClassNotFoundException e) {
			LOG.error("Could not load postgres driver!", e);
		} catch (SQLException e) {
			LOG.error("Could not connect to db!", e);
		}
	}

	/**
	 * Returns a set of all hard functional dependencies in the db
	 * 
	 * @return set of all hard FDs
	 * @throws TableNotFoundException if passed table does not exist in db
	 */
	private Set<FunctionalDependency> findAllHardFds(Relation rel) throws TableNotFoundException {
		Set<FunctionalDependency> hardFdSet = new HashSet<FunctionalDependency>();
		// Check to see if table doesn't exist
		if(!dbHelper.tableExists(rel)) {
			LOG.warn("Attempted to find hard fds of non-existant table {}", rel.getName());
			throw new TableNotFoundException();
		}
		
		

		return hardFdSet;
	}

	/**
	 * @return the dbHelper
	 */
	public DBHelper getDbHelper() {
		return dbHelper;
	}

}
