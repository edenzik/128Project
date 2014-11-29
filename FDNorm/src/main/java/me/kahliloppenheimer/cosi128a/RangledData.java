package me.kahliloppenheimer.cosi128a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;


@SuppressWarnings("restriction")
public class RangledData {

	private final HttpExchange t;
	private final DBHelper dbHelper;
	private final Logger LOG = LoggerFactory.getLogger(RangledData.class);
	private Map<String, PostgresAttType> inferredTypes;

	public RangledData(HttpExchange t, DBHelper dbHelper) {
		this.t = t;
		this.dbHelper = dbHelper;
	}

	/**
	 * Creates the initial table with inferred types and populates it
	 * with all of the passed data.
	 * 
	 */
	public void createAndPopulateInitialTable() {
		BufferedReader br = null;
		String tableName = null;
		try {
			br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
			tableName = createInitialTable(br);
		} catch (IOException e) {
			LOG.error("Failed to create table: {}\n{}", tableName, e.getMessage());
		}
		try {
			populateInitialTable(tableName, br);
		} catch(IOException e) {
			LOG.error("Failed to populate table: {}\n{}", tableName, e.getMessage());
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch(IOException e) {
					LOG.error("Failed to close Buffered Reader!");
				}
			}
		}
	}

	/**
	 * Creates the initial table for the data with inferred types. Returns
	 * the name of the created table.
	 * 
	 * @param br
	 * @return
	 * @throws IOException
	 */
	private String createInitialTable(BufferedReader br) throws IOException {
		String tableName = null;
		String firstLine = br.readLine();
		if(firstLine != null) {
			try {
				tableName = QueryHelper.DEFAULT_TABLE_NAME + dbHelper.countTables();
			} catch (SQLException e) {
				LOG.error("Failed to count tables: {}", e.getMessage());
			}
			String[] headers = parseLine(firstLine);
			// TODO: Make this guess at anticipated read chars more sophisticated
			br.mark(QueryHelper.TYPE_INFERENCE_SAMPLE_SIZE * headers.length * 100);
			Map<String, List<String>> headersToSampleValues = getSampleValues(headers, br);
			br.reset();
			// Make the table name by appending the current number of tables to the
			// default table name
			inferredTypes = QueryHelper.inferTableTypes(headers, headersToSampleValues);
			String createTableQuery = QueryHelper.getCreateTableQuery(tableName, inferredTypes);
			dbHelper.executeUpdate(createTableQuery);

		} else {
			LOG.error("Empty CSV file passed to server!");
		}
		return tableName;
	}

	/**
	 * Given a BufferedReader pointing to the starting data values (not header values) and the
	 * name of the created table (which must exist), this method populates the table with the
	 * data values from the reader.
	 * 
	 * @param tableName
	 * @param br
	 * @throws IOException
	 */
	private void populateInitialTable(String tableName, BufferedReader br) throws IOException {
		if(!dbHelper.tableExists(tableName)) {
			LOG.error("{} does not exist as a table in the database!", tableName);
		}
		String nextLine = null;
		while((nextLine = br.readLine()) != null) {
			String[] atts = parseLine(nextLine);
			String insertQuery = QueryHelper.getInsertQuery(inferredTypes, atts, tableName);
			dbHelper.executeUpdate(insertQuery);
		}
	}

	// Given a string of headers and a buffered reader positioned at the start of all of the values
	// returns a mapping of headers to sample values (i.e. name -> {"John", "Jacob", "Jingleheimer"})
	private Map<String, List<String>> getSampleValues(String[] headers, BufferedReader br) throws IOException {
		LOG.info("Starting to gather sample values from the data...");
		Map<String, List<String>> headerValueMap = new HashMap<String, List<String>>();
		String nextLine = null;
		int lineCount = 0;
		while((nextLine = br.readLine()) != null && lineCount < QueryHelper.TYPE_INFERENCE_SAMPLE_SIZE) {
			++lineCount;
			String[] parsedLine = parseLine(nextLine);
			for(int i = 0; i < parsedLine.length; ++i) {
				String key = headers[i];
				String value = parsedLine[i];
				// If list already exists, simply append value to it
				if(headerValueMap.containsKey(key)) {
					headerValueMap.get(key).add(value);
				}
				// Otherwise, create new list with the first value
				else {
					// Notable the size should be of the default sample size for type inference
					List<String> valueList = new ArrayList<String>(QueryHelper.TYPE_INFERENCE_SAMPLE_SIZE);
					valueList.add(value);
					headerValueMap.put(key, valueList);
				}
			}
		}
		LOG.info("Finished gathering sample values from the data!");
		return headerValueMap;
	}

	/**
	 * Returns a string array of all entries from one line of CSV
	 * 
	 * @param line
	 * @return
	 */
	private String[] parseLine(String line) {
		String[] splitLine =  line.split(",");
		for(int i = 0; i < splitLine.length; ++i) {
			splitLine[i] = splitLine[i].trim();
		}
		return splitLine;
	}
}
