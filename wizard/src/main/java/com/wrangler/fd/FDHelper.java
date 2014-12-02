package com.wrangler.fd;

import java.util.HashSet;
import java.util.Set;

import com.wrangler.load.Attribute;
import com.wrangler.load.DBHelper;

/**
 * Object to help detect and analyze functional dependencies in a given database
 * 
 * @author kahliloppenheimer
 *
 */
public class FDHelper {
	
	private final DBHelper dbHelper;
	private Set<Attribute> tableAtts;
	private Set<FunctionalDependency> hardFdSet;
	
	public FDHelper(DBHelper dbHelper) {
		this.dbHelper = dbHelper;
		this.tableAtts = dbHelper.getTableAttributes();
		this.hardFdSet = findAllHardFds();
	}

	/**
	 * Returns a set of all hard functional dependencies in the db
	 * 
	 * @return
	 */
	private Set<FunctionalDependency> findAllHardFds() {
		Set<FunctionalDependency> hardFdSet = new HashSet<FunctionalDependency>();
		return hardFdSet;
	}

	/**
	 * @return the dbHelper
	 */
	public DBHelper getDbHelper() {
		return dbHelper;
	}

}
