package com.wrangler.load;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrangler.extract.CSVFormatException;
import com.wrangler.normalization.Normalizer;

/**
 * Class to ease in the generation of queries (i.e. update and create table queries
 * given just a list of attributes)
 * 
 * @author kahliloppenheimer
 *
 */
public class QueryHelper {

	public static final String DEFAULT_TABLE_NAME = "TABLE";
	private static final Logger LOG = LoggerFactory.getLogger(QueryHelper.class);

	/**
	 * ONLY USED FOR UNT-TESTING
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException {
		//		String[] test = {"4", "4.9", ".65", ".65.7", "7.33", "S23", "07.21", "Jerry", "$415", "415.000"};
		//		for(String s: test) {
		//			System.out.println(s + " : " + inferValueType(s));
		//		}
		//
		//		String header1 = "ShouldBeDecimal1";
		//		List<String> values1 = Arrays.asList(new String[] {"43", "45", "12", "13", "5"});
		//		String header2 = "ShouldBeVarchar1";
		//		List<String> values2 = Arrays.asList(new String[] {"Jerry", "John", "K-dawg"});
		//		String header3 = "ShouldBeVarchar2";
		//		List<String> values3 = Arrays.asList(new String[] {"4", "53.2", ".79", "$5"});
		//
		//		Map<String, List<String>> headersToValues = new HashMap<String, List<String>>();
		//		headersToValues.put(header1, values1);
		//		headersToValues.put(header2, values2);
		//		headersToValues.put(header3, values3);
		//
		//		Map<String, PostgresAttType> inferredTypes = inferTableTypes(headersToValues);
		//		System.out.println(inferredTypes);
		//
		//		System.out.println(getInsertQuery(inferredTypes, new ArrayList<String>(Arrays.asList(new String[] {"6", "Jerry", "John"})), new Relation("PERSON", null)));
		//
		//		for(String s: headersToValues.keySet()) {
		//			System.out.println(s + " : " + QueryHelper.inferColumnType(s, headersToValues.get(s)));
		//		}

		Host host = HostFactory.createDefaultHost();
		Database db = null;
		try {
			db = DatabaseFactory.createDatabase("kahliloppenheimer", host);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Relation rel = RelationFactory.createExistingRelation("table152", db);
		Set<Relation> normalized = (Normalizer.newInstance(rel)).bcnf();
		Relation rel1 = normalized.iterator().next();
		System.out.println(rel1.getAttributes());
		System.out.println(getSelectQueryForAtts(rel, rel1.getAttributes()));
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
	 * @throws CSVFormatException 
	 */
	public static String getCreateTableQuery(Relation rel, Map<String, PostgresAttType> inferredTypes) throws CSVFormatException {
		// Ugliest but apparently most effective way to convert a set to an array
		String[] headers = inferredTypes.keySet().toArray(new String[inferredTypes.keySet().size()]);
		if(headers.length == 0) {
			throw new CSVFormatException("Trying to create table with blank list of attributes!");
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
			LOG.warn("No sample data for column {}! Assuming text...", colName);
			return PostgresAttType.newText();
		}
		// Check to see if any elements differ in inference type.
		// If so, just assume default type for all
		boolean typesDiffer = false;
		// Go through, check to see if any types mismatch (i.e. we should just assume text)
		for(int i = 0; i < columnVals.size() - 1; ++i) {
			// Check if the types differ
			if(!PostgresAttType.valueOf(columnVals.get(i)).equals(PostgresAttType.valueOf(columnVals.get(i + 1)))) {
				typesDiffer = true;
			}
		}
		if(typesDiffer) {
			return PostgresAttType.newText();
		} else {
			// If there are no mismatches, simply return the common type
			return PostgresAttType.valueOf(columnVals.get(0));
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
	 * Returns the selection query to select atts from rel with only a select
	 * and from clause (i.e. no where clause). TODO: Add Java-8 style lambda
	 * passing for where clause predicates?
	 * 
	 * @param rel
	 * @param atts
	 * @return
	 */
	public static String getSelectQueryForAtts(Relation rel, Set<Attribute> atts) {
		if(atts.size() < 1) {
			throw new IllegalArgumentException("Attributes must have at least one element!");
		}
		Iterator<Attribute> iter = atts.iterator();
		StringBuilder query = new StringBuilder();
		query.append(String.format("SELECT %s", iter.next()));
		while(iter.hasNext()) {
			query.append(String.format(", %s" ,iter.next().getName()));
		}
		query.append(String.format(" FROM %s" ,rel.getName()));

		return query.toString();
	}

	/**
	 * Returns true if a given column is a char type (i.e. varchar)
	 * @param header
	 * @param inferredTypes
	 * @return
	 */
	private static boolean isCharType(String header,
			Map<String, PostgresAttType> inferredTypes) {
		return inferredTypes.get(header).isCharType();
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
	/**
	 * Given a List of Lists of Strings representing a CSV file
	 * and a relation to generate the insert statement for, returns
	 * one insert statement of the form:
	 * 
	 * INSERT INTO foo (bar, baz) 
	 * VALUES ('bar1', 'baz1'), 
     * 		('bar1', 'baz1'),
     * 		('bar2', 'baz2'),
     * 		('bar3', 'baz3');
     * 
	 * @param csvData
	 * @param rel
	 * @return
	 * @throws SQLException
	 */
	public static String getMultipleInsertQuery(
			List<List<String>> csvData, Relation rel) throws SQLException {
		Set<Attribute> cols = rel.getAttributes();
		if(cols.size() < 1) {
			throw new AssertionError("Relation should have at least one attribute!");
		}
		StringBuilder query = new StringBuilder();

		// Build up the grossness that is an SQL query...
		Iterator<Attribute> attrIter = cols.iterator();
		query.append(String.format("INSERT INTO %s (%s", rel.getName(), attrIter.next().getName()));
		while(attrIter.hasNext()) {
			query.append(String.format(", %s", attrIter.next().getName()));
		}
		query.append(")\nVALUES\t");

		Iterator<List<String>> tupleIter = csvData.iterator();
		query.append(listToInsert(tupleIter.next(), cols));
		while(tupleIter.hasNext()) {
			query.append(",\n\t");
			query.append(listToInsert(tupleIter.next(), cols));
		}
		query.append(";");
		return query.toString();
	}
	/**
	 * Takes a list of strings for the next row and an iterator over the
	 * attributes of the tables (for the types), and generates the inline
	 * SQL values statement, i.e. (x, y) or ('x', 'y')
	 * 
	 * @param tuple
	 * @param headerIter
	 * @return
	 */
	private static String listToInsert(List<String> tuple,
			Set<Attribute> headers) {
		if(tuple.size() == 0) {
			LOG.warn("Ignored empty CSV row!");
			return "";
		} else if(tuple.size() != headers.size()) {
			LOG.warn("Tuple {} does not match schema {}!", tuple, headers);
		}

		// Iterate through values concurrently with iteration through headers
		// to know which types the values are (i.e. if they need quotes around
		// them)
		Iterator<Attribute> headerIter = headers.iterator();
		Iterator<String> valueIter = tuple.iterator();

		StringBuilder sql = new StringBuilder();
		sql.append("(");
		sql.append(getNextTuple(headerIter.next(), valueIter.next(), true));

		while(valueIter.hasNext()) {
			sql.append(getNextTuple(headerIter.next(), valueIter.next(), false));
		}
		
		sql.append(")");
		
		return sql.toString();
	}
	/**
	 * Given a column and a value, returns the next tuple in an insert friendly
	 * way (i.e. as NULL, surrounded by quotes, or just the value
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	private static String getNextTuple(Attribute column, String value, boolean firstTuple) {
		String s = null;
		if(value.matches("NULL")) {
			s = "NULL";
		} else {
			String surrounding = getSurroundingChar(column);
			s = String.format("%s%s%s", surrounding, value, surrounding);
		}
		// First tuple is just the value, other tuples need comma first
		if(firstTuple) {
			return s;
		} else {
			return ", " + s;
		}
	}
	/**
	 * Given an attribute a, determines based on the type whether insert
	 * statements around values for this attribute need be surrounded
	 * by any special characters (i.e. ' for varchar)
	 * 
	 * @param a
	 * @return
	 */
	private static String getSurroundingChar(Attribute a) {
		if(a.getAttType().isCharType()) {
			return "'";
		}
		return "";
	}

}
