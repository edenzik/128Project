package com.wrangler.extract;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrangler.load.Database;
import com.wrangler.load.PostgresAttType;
import com.wrangler.load.QueryHelper;
import com.wrangler.load.Relation;


public class WrangledDataExtractor {

	// All headers (i.e. column/attribute names) for the data
	private List<String> headers;
	// Each tuple is stored as a list of String
	// The entire data collection is a list of the tuples
	private List<List<String>> wrangledData;
	// Database to which this "extracts" the data
	private final Database db;
	// Used for logging important logging
	private final Logger LOG = LoggerFactory.getLogger(WrangledDataExtractor.class);
	// Maps each header value to its inferred data type (i.e. numeric, varchar, date, etc.) 
	private Map<String, PostgresAttType> inferredTypes;

	public WrangledDataExtractor(String inputData, Database db) throws IOException {
		this.headers = new ArrayList<String>();
		this.wrangledData = new ArrayList<List<String>>();
		loadInputStream(inputData, headers, wrangledData);
		this.db = db;
	}

	/**
	 * Takes input Wrangled Data and reads the headers (i.e. column/attribute names)
	 * into the passed header list and then reads each line containing actual data
	 * and adds each line as a List to the maintained List of Lists 
	 * 
	 * @param inputData
	 * @return
	 * @throws IOException
	 */
	private void loadInputStream(String inputData, List<String> colNames, List<List<String>> wrangledData) throws IOException {
		LOG.info("Reading input data into memory buffer...");
		CSVParser parser = CSVParser.parse(inputData, CSVFormat.DEFAULT);
		for (CSVRecord tuple : parser.getRecords()){
			// Add header row to header collection
			if(tuple.getRecordNumber() == 1) {
				for(String attName : tuple) {
					colNames.add(attName);
				}
				continue;
			}

			List<String> tupleList = new ArrayList<String>();
			// Keeps track of whether or not we have seen any
			// blank values in a given tuple
			boolean hasBlankValue = false;
			for(String value : tuple) {
				// If we see blank tuple, exit loop
				if(value.length() == 0) {
					hasBlankValue = true;
					break;
				}
				tupleList.add(value.trim());
			}
			// Only add tuples that did not have any blank values
			if(!hasBlankValue) {
				wrangledData.add(tupleList);
			}
		}
		LOG.debug("this.wrangledData = {}",wrangledData);
		LOG.info("Finished reading input data into memory buffer!");
		// Make sure we at least read in some non-header data
		if(this.wrangledData.size() == 0) {
			LOG.error("Input data contained no actual data!");
		}
	}

	/**
	 * Creates the initial table with inferred types and populates it
	 * with all of the passed data.
	 * 
	 */
	public void createAndPopulateInitialTable() {
		Relation rel = null;
		try {
			rel = createInitialTable(this.wrangledData);
			LOG.info("Created initial database table!");
		} catch (IOException e) {
			LOG.error("Failed to create table: {}\n{}", rel, e.getMessage());
		}
		try {
			populateInitialTable(rel);
			LOG.info("Populated initial database table!");
		} catch(IOException e) {
			LOG.error("Failed to populate table: {}", rel, e);
		} 
	}

	/**
	 * Creates the initial table for the data with inferred types. Returns
	 * the name of the created table.
	 * 
	 * @param wrangledData
	 * @return
	 * @throws IOException
	 */
	private Relation createInitialTable(List<List<String>> wrangledData) throws IOException {
		Relation rel = null;
		// Now we need to figure out a unique name for this new table
		try {
			String tableName = QueryHelper.DEFAULT_TABLE_NAME + db.getDbHelper().countTables();
			rel = new Relation(tableName, db);
		} catch (SQLException e) {
			LOG.error("Failed to count tables!", e);
		}
		Map<String, List<String>> headersToSampleValues = getSampleValues(this.headers, this.wrangledData);
		inferredTypes = QueryHelper.inferTableTypes(headersToSampleValues);
		String createTableQuery = QueryHelper.getCreateTableQuery(rel, inferredTypes);
		db.getDbHelper().executeUpdate(createTableQuery);

		return rel;
	}

	/**
	 * Given a BufferedReader pointing to the starting data values (not header values) and the
	 * name of the created table (which must exist), this method populates the table with the
	 * data values from the reader.
	 * 
	 * @param tableName
	 * @param sc
	 * @throws IOException
	 */
	private void populateInitialTable(Relation rel) throws IOException {
		if(!db.getDbHelper().tableExists(rel)) {
			LOG.error("{} does not exist as a table in the database!", rel);
		}
		for(List<String> nextTuple : wrangledData) {
			String insertQuery = null;
			if(nextTuple.size() != 0) {
				insertQuery = QueryHelper.getInsertQuery(inferredTypes, nextTuple, rel);
				db.getDbHelper().executeUpdate(insertQuery);
			}
		}
	}

	/**
	 * Given a string of headers and a String containing all of the values,
	 * returns a mapping of headers to sample values (i.e. name -> {"John", "Jacob", "Jingleheimer"})
	 * 
	 * @param headers
	 * @param wrangledData
	 * @return
	 * @throws IOException
	 */
	private Map<String, List<String>> getSampleValues(List<String> headers, List<List<String>> wrangledData) throws IOException {
		LOG.info("Starting to gather sample values from the data...");
		Map<String, List<String>> headersToSampleValues = new LinkedHashMap<String, List<String>>();
		// Go through each line (tuple) and adds each attribute value to our map
		// with the proper corresponding header
		for(List<String> nextTuple: wrangledData) {
			for(int i = 0; i < nextTuple.size(); ++i) {
				String key = headers.get(i);
				String value = nextTuple.get(i);
				// If list already exists, simply append value to it
				if(headersToSampleValues.containsKey(key)) {
					headersToSampleValues.get(key).add(value);
				}
				// Otherwise, create new list with the first value
				else {
					// Make sure to save enough rows per value as we have rows of data
					List<String> valueList = new ArrayList<String>(wrangledData.size());
					valueList.add(value);
					headersToSampleValues.put(key, valueList);
				}
			}
		}
		LOG.info("Finished gathering sample values from the data!");
		return headersToSampleValues;
	}

	/**
	 * @return the db
	 */
	public Database getDb() {
		return db;
	}
}
