package com.wrangler.load;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to ease in the generation of queries (i.e. update and create table queries
 * given just a list of attributes)
 * 
 * @author kahliloppenheimer
 *
 */
public class QueryHelper {

	public static final String DEFAULT_TABLE_NAME = "TABLE";
	public static final PostgresAttType DEFAULT_ATT_TYPE = PostgresAttType.VARCHAR;
	private static final Logger LOG = LoggerFactory.getLogger(QueryHelper.class);

	/**
	 * ONLY USED FOR UNT-TESTING
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException {
		String[] test = {"4", "4.9", ".65", ".65.7", "7.33", "S23", "07.21", "Jerry", "$415", "415.000"};
		for(String s: test) {
			System.out.println(s + " : " + inferValueType(s));
		}

		String header1 = "ShouldBeDecimal1";
		List<String> values1 = Arrays.asList(new String[] {"43", "45", "12", "13", "5"});
		String header2 = "ShouldBeVarchar1";
		List<String> values2 = Arrays.asList(new String[] {"Jerry", "John", "K-dawg"});
		String header3 = "ShouldBeVarchar2";
		List<String> values3 = Arrays.asList(new String[] {"4", "53.2", ".79", "$5"});

		Map<String, List<String>> headersToValues = new HashMap<String, List<String>>();
		headersToValues.put(header1, values1);
		headersToValues.put(header2, values2);
		headersToValues.put(header3, values3);

		Map<String, PostgresAttType> inferredTypes = inferTableTypes(headersToValues);
		System.out.println(inferredTypes);

		System.out.println(getInsertQuery(inferredTypes, new ArrayList<String>(Arrays.asList(new String[] {"6", "Jerry", "John"})), new Relation("PERSON", null)));

		for(String s: headersToValues.keySet()) {
			System.out.println(s + " : " + QueryHelper.inferColumnType(s, headersToValues.get(s)));
		}

	}
	/**
	 * Returns a create table command with the passed table name and
	 * String array of schema attributes. Notably, this will sample
	 * the first couple of tuples of each attribute to try to infer
	 * type (i.e. int, float, date, etc.) based on basic pattern matching.
	 * 
	 * @param rel
	 * @param inferredTypes 
	 * @return
	 */
	public static String getCreateTableQuery(Relation rel, Map<String, PostgresAttType> inferredTypes) {
		// Ugliest but apparently most effective way to convert a set to an array
		String[] headers = inferredTypes.keySet().toArray(new String[inferredTypes.keySet().size()]);
		if(headers.length == 0) {
			throw new IllegalArgumentException("Trying to create table with blank list of attributes!");
		}
		StringBuilder attList = new StringBuilder();
		attList.append("(");
		attList.append(headers[0] + " " + inferredTypes.get(headers[0]));
		for(int i = 1; i < headers.length; ++i) {
			attList.append(", " + headers[i] + " " + inferredTypes.get(headers[i])); 
		}
		attList.append(")");
		return "CREATE TABLE " + rel + attList.toString() + ";";
	}

	/**
	 * Performs basic pattern matching to infer the data type of the given
	 * column and sample of values (found in the passed map)
	 * 
	 * @param header
	 * @param wrangledData
	 * @return
	 */
	private static PostgresAttType inferColumnType(String colName, List<String> columnVals) {
		if(columnVals == null || columnVals.size() == 0) {
			LOG.warn("No sample data for column {}! Assuming varchar...", colName);
			return DEFAULT_ATT_TYPE;
		}
		// Check to see if any elements differ in inference type.
		// If so, just assume default type for all
		for(int i = 0; i < columnVals.size() - 1; ++i) {
			if(!inferValueType(columnVals.get(i)).equals(inferValueType(columnVals.get(i + 1)))) {
				return DEFAULT_ATT_TYPE;
			}
		}
		// If there are no mismatches, simply return the common type
		return inferValueType(columnVals.get(0));
	}

	/**
	 * Does basic pattern matching to infer the data type of the given value
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unused")
	private static PostgresAttType inferValueType(String string) {
		// First try to read it as BigDecimal
		try {
			BigDecimal asDecimal = new BigDecimal(string);
			return PostgresAttType.DECIMAL;
		} catch(NumberFormatException e) {
			// If that didn't work just return default
			return DEFAULT_ATT_TYPE;
		}
	}

	/**
	 * Returns a String representing an insert query with the given values
	 * 
	 * @param atts
	 * @param headers 
	 * @return
	 */
	public static String getInsertQuery(Map<String, PostgresAttType> inferredTypes, List<String> atts, Relation rel) {
		// Ugly but effective way to convert Set<String> to String[]
		String[] headers = inferredTypes.keySet().toArray(new String[inferredTypes.size()]);
		if(atts.size() == 0) {
			throw new IllegalArgumentException("Trying to insert blank list of values!");
		}
		// Build SQL string for column names
		StringBuilder columnList = new StringBuilder();
		columnList.append("(");
		columnList.append(headers[0]);
		for(int i = 1; i < headers.length; ++i) {
			columnList.append(", " + headers[i]);
		}
		columnList.append(")");

		// Build SQL String for the values
		StringBuilder valueList = new StringBuilder();
		valueList.append("(");
		if(isCharType(headers[0], inferredTypes)) {
			valueList.append("'" + atts.get(0) + "'");
		} else {
			valueList.append(atts.get(0));
		}
		for(int i = 1; i < atts.size(); ++i) {
			valueList.append(", ");
			if(isCharType(headers[i], inferredTypes)) {
				valueList.append("'" + atts.get(i) + "'");
			} else {
				valueList.append(atts.get(i));
			}
		}
		valueList.append(")");
		String query = "INSERT INTO " + rel + " " + columnList + " VALUES " + valueList.toString() + ";";
		return query;
	}

	/**
	 * Returns true if a given column is a char type (i.e. varchar)
	 * @param header
	 * @param inferredTypes
	 * @return
	 */
	private static boolean isCharType(String header,
			Map<String, PostgresAttType> inferredTypes) {
		return inferredTypes.get(header).equals(PostgresAttType.VARCHAR);
	}

	/**
	 * Uses basic pattern matching to map all of the tables columns to their types
	 * @param headersToSampleValues 
	 * 
	 * @param headers
	 * @param wrangledData
	 * @return
	 */
	public static Map<String, PostgresAttType> inferTableTypes(Map<String, List<String>> headersToSampleValues) {
		LOG.info("Inferring types for database columns...");
		Map<String, PostgresAttType> columnTypes = new LinkedHashMap<String, PostgresAttType>();
		Set<String> headers = headersToSampleValues.keySet();
		for(String s: headers) {
			columnTypes.put(s, inferColumnType(s, headersToSampleValues.get(s)));
		}
		LOG.debug("Inferred types are: {}", columnTypes);
		return columnTypes;
	}

}
