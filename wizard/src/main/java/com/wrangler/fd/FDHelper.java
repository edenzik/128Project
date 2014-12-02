package com.wrangler.fd;

import java.util.Set;

import com.wrangler.load.DBHelper;

/**
 * Object to help detect and analyze functional dependencies in a given database
 * 
 * @author kahliloppenheimer
 *
 */
public class FDHelper {
	
	private final DBHelper dbHelper;
	private Set<FunctionalDependency> fdSet;
	
	public FDHelper(DBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	/**
	 * @return the dbHelper
	 */
	public DBHelper getDbHelper() {
		return dbHelper;
	}

}
