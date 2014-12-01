package me.kahliloppenheimer.cosi128a;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WrangledDataExtractor {

	// All headers (i.e. column/attribute names) for the data
	private List<String> headers;
	// All actual data stored as a list of strings
	private List<String> wrangledData;
	// Manager of any direct interaction with the database
	private final DBHelper dbHelper;
	// Used for logging important logging
	private final Logger LOG = LoggerFactory.getLogger(WrangledDataExtractor.class);
	// Maps each header value to its inferred data type (i.e. numeric, varchar, date, etc.) 
	private Map<String, PostgresAttType> inferredTypes;

	public WrangledDataExtractor(String inputData, DBHelper dbHelper) throws IOException {
		this.headers = new ArrayList<String>();
		this.wrangledData = new ArrayList<String>();
		loadInputStream(inputData, headers, wrangledData);
		this.dbHelper = dbHelper;
	}
	
	/**
	 * Takes input Wrangled Data and reads the headers (i.e. column/attribute names)
	 * into the passed header list and then reads each line containing actual data
	 * and adds them to the passed data list
	 * 
	 * @param inputData
	 * @return
	 * @throws IOException
	 */
	private void loadInputStream(String inputData, List<String> colNames, List<String> data) throws IOException {
		LOG.info("Reading input data into memory buffer...");
		Scanner sc = new Scanner(inputData);
		// Parse first line for the data headers
		if(sc.hasNextLine()) {
			this.headers = parseLine(sc.nextLine());
		} else {
			LOG.error("Input data contained no headers!");
		}
		// Now parse rest of the file
		while(sc.hasNextLine()){
			this.wrangledData.add(sc.nextLine());
		}
		LOG.info("Finished reading input data into memory buffer!");
		// Make sure we at least read in some non-header data
		if(this.wrangledData.size() == 0) {
			LOG.error("Input data contained no actual data!");
		}
		sc.close();
	}

	/**
	 * Creates the initial table with inferred types and populates it
	 * with all of the passed data.
	 * 
	 */
	public void createAndPopulateInitialTable() {
		String tableName = null;
		try {
			tableName = createInitialTable(this.wrangledData);
		} catch (IOException e) {
			LOG.error("Failed to create table: {}\n{}", tableName, e.getMessage());
		}
		try {
			populateInitialTable(tableName);
		} catch(IOException e) {
			LOG.error("Failed to populate table: {}\n{}", tableName, e.getMessage());
		} 
	}

	/**
	 * Creates the initial table for the data with inferred types. Returns
	 * the name of the created table.
	 * @param wrangledData2 
	 * 
	 * @param wrangledData2
	 * @return
	 * @throws IOException
	 */
	private String createInitialTable(List<String> wrangledData) throws IOException {
		String tableName = null;
		// Now we need to figure out a unique name for this new table
		try {
			tableName = QueryHelper.DEFAULT_TABLE_NAME + dbHelper.countTables();
		} catch (SQLException e) {
			LOG.error("Failed to count tables!", e);
		}
		Map<String, List<String>> headersToSampleValues = getSampleValues(this.headers, this.wrangledData);
		inferredTypes = QueryHelper.inferTableTypes(headersToSampleValues);
		String createTableQuery = QueryHelper.getCreateTableQuery(tableName, inferredTypes);
		dbHelper.executeUpdate(createTableQuery);

		return tableName;
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
	private void populateInitialTable(String tableName) throws IOException {
		if(!dbHelper.tableExists(tableName)) {
			LOG.error("{} does not exist as a table in the database!", tableName);
		}
		for(String nextLine: wrangledData) {
			List<String> atts = parseLine(nextLine);
			String insertQuery = QueryHelper.getInsertQuery(inferredTypes, atts, tableName);
			dbHelper.executeUpdate(insertQuery);
		}
	}

	/**
	 * Given a string of headers and a String containing all of the values,
	 * returns a mapping of headers to sample values (i.e. name -> {"John", "Jacob", "Jingleheimer"})
	 * 
	 * @param headers
	 * @param wrangledData2
	 * @return
	 * @throws IOException
	 */
	private Map<String, List<String>> getSampleValues(List<String> headers, List<String> data) throws IOException {
		LOG.info("Starting to gather sample values from the data...");
		Map<String, List<String>> headersToSampleValues = new LinkedHashMap<String, List<String>>();
		// Go through each line (tuple) and adds each attribute value to our map
		// with the proper corresponding header
		for(String nextLine: wrangledData) {
			List<String> parsedLine = parseLine(nextLine);
			for(int i = 0; i < parsedLine.size(); ++i) {
				String key = headers.get(i);
				String value = parsedLine.get(i);
				// If list already exists, simply append value to it
				if(headersToSampleValues.containsKey(key)) {
					headersToSampleValues.get(key).add(value);
				}
				// Otherwise, create new list with the first value
				else {
					// Make sure to save enough rows per value as we have rows of data
					List<String> valueList = new ArrayList<String>(this.wrangledData.size());
					valueList.add(value);
					headersToSampleValues.put(key, valueList);
				}
			}
		}
		LOG.info("Finished gathering sample values from the data!");
		return headersToSampleValues;
	}

	/**
	 * Returns a string array of all entries from one line of CSV
	 * 
	 * @param line
	 * @return
	 */
	private List<String> parseLine(String line) {
		String[] splitLine =  line.split(",");
		for(int i = 0; i < splitLine.length; ++i) {
			splitLine[i] = splitLine[i].trim();
		}
		return Arrays.asList(splitLine);
	}
}
