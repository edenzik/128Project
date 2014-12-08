package com.wrangler.load;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.wrangler.constraint.ForeignKey;
import com.wrangler.extract.CSVFormatException;
import com.wrangler.fd.FDFactory;
import com.wrangler.fd.SoftFD;

/**
 * Class to abstract away from boilerplate of JDBC connection
 * 
 * @author kahliloppenheimer
 *
 */
public class DBHelper {

	private SimpleJDBCConnectionPool pool;
	private static final Logger LOG = LoggerFactory.getLogger(DBHelper.class);
	private final Database db;
	private final Connection DEFAULT_CONNECTION;

	/**
	 * Used only for unit testing
	 * 
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Database db = DatabaseFactory.createDatabase("kahliloppenheimer", HostFactory.createDefaultHost());
		Relation rel = RelationFactory.createExistingRelation("kahlil3", db);
		Set<Attribute> attrs = rel.getAttributes();
		Attribute from = null, to = null;
		for(Attribute a : attrs) {
			if(a.getName().equals("school")) {
				from = a;
			} else if(a.getName().equals("city")) {
				to = a;
			}
		}
		SoftFD soft = FDFactory.createSoftFD(from, to);
		Map<String, Map<String, Double>> violations = soft.getViolations();
		Map<String, String> corrections = new HashMap<String, String>();
		corrections.put("Brandeis", "Waltham");
		db.getDbHelper().fixAllViolations(soft, corrections);
		System.out.println("VIOLATIONS = " + violations);

		//			Normalizer norm = Normalizer.newInstance(rel);
		//			Set<Relation> normalized = norm.bcnf();
		//			for(Relation r: normalized) {
		//				Set<ForeignKey> fks = r.getFks();
		//				if(!fks.isEmpty()) {
		//					LOG.debug("{} has fks {}", r, fks);
		//				}
		//			}
		//			rel.decomposeInto(normalized);
		//		} catch (ClassNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (SQLException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}

	/**
	 * Constructs a DatabaseHelper object to help with the passed Database
	 * 
	 * @param db
	 * @throws ClassNotFoundException if postgres driver can't be loaded
	 * @throws SQLException if connection can't be established to DB
	 */
	public DBHelper(Database db) throws ClassNotFoundException, SQLException {
		this.db = db;
		Class.forName("org.postgresql.Driver");
		LOG.info("Initialized postgresql JDBC Driver");
		String uri = "jdbc:postgresql://" + db.getHost().getIp() + "/" + db.getName();
		LOG.info("Connecting to {}...", uri);
		pool = new SimpleJDBCConnectionPool("org.postgresql.Driver", uri, db.getHost().getRole(), db.getHost().getPass());
		LOG.info("Connected to {}!", db);
		(DEFAULT_CONNECTION = pool.reserveConnection()).setAutoCommit(true);
	}

	/**
	 * Returns a new Connection from the Connection pool that
	 * auto-commits
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		return DEFAULT_CONNECTION;
		//		Connection conn = pool.reserveConnection();
		//		conn.setAutoCommit(true);
		//		return conn;
	}

	/**
	 * Executes the given update query (i.e. "insert into foo values ("bar");").
	 * Returns true if it was successful, false otherwise.
	 * 
	 * @param updateQuery update Query to be executed
	 * @throws SQLException
	 */
	public boolean executeUpdate(String query) {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stmt = conn.createStatement();
			LOG.info("{}: {}", getDb(), query);
			stmt.executeUpdate(query);
			// Fix to protect against delayed commits (especially
			// relevant for create table statements that cause later
			// checks to fail.
			conn.setAutoCommit(false);
			conn.commit();
			conn.setAutoCommit(true);
			return true;
		} catch(SQLException e) {
			LOG.error("", e);
			return false;
		} finally {
			if(conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	/**
	 * Gets a list of all the tables in the specified schema
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public Set<Relation> getRelations() {
		Connection conn = null;
		try {
			Set<Relation> tableSet = new LinkedHashSet<Relation>();
			conn = getConnection();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet tableRS = m.getTables(null, "public", "%", new String[] {"TABLE"} );
			while(tableRS.next()) {
				String tableName = tableRS.getString(3);
				Relation rel = RelationFactory.createExistingRelation(tableName, getDb());
				tableSet.add(rel);
			}
			return tableSet;
		} catch (SQLException e) {
			LOG.error("Could not retrieve tables!\n", e);
			return null;
		} finally {
			pool.releaseConnection(conn);
		}
	}

	/**
	 * Executes the given select query (i.e. "select * from foo;")
	 * 
	 * @param query the query to be executed
	 * @return
	 * @throws SQLException
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		LOG.info(query);
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stmt = conn.createStatement();
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			throw e;
		} finally {
			pool.releaseConnection(conn);
		}
	}

	/**
	 * Checks if the result of this query exists
	 * 
	 * @param if the result of this query is empty or not (true if not empty)
	 * @return
	 * @throws SQLException
	 */
	public boolean exists(String query) {
		LOG.info(query);
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stmt = conn.createStatement();
			return stmt.executeQuery(query).next();
		} catch (SQLException e) {
			LOG.error("", e);
		} finally {
			pool.releaseConnection(conn);
		}
		return false;
	}

	/**
	 * Closes the connection to the given database
	 * 
	 * @throws SQLException
	 */
	public void close() {
		pool.destroy();
		LOG.info("Closed database connection!");
	}

	public void open() {
		LOG.info("Opened new database connection!");
		String uri = "jdbc:postgresql://" + db.getHost().getIp() + "/" + db.getName();
		try {
			pool = new SimpleJDBCConnectionPool("org.postgresql.Driver", uri, db.getHost().getRole(), db.getHost().getPass());
		} catch (SQLException e) {
			LOG.error("", e);
		}

	}



	/**
	 * Returns true if a given table exists and false if it does not
	 * 
	 * @param rel
	 * @return
	 */
	public boolean tableExists(Relation rel) {
		Connection conn = null;
		try {
			conn = getConnection();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, rel.getName().toLowerCase(), null);
			// Table exists
			if(tables.next()) {
				return true;
			}
		}
		catch(SQLException e) {
			LOG.error("Could not check if table exists!\n", e);
			return false;
		} finally {
			pool.releaseConnection(conn);
		}
		return false;
	}

	/**
	 * Returns true if a given database exists
	 * 
	 * @param database name
	 * @return
	 * @throws SQLException
	 */
	public boolean databaseExists(String databaseName){
		Connection conn = null; 
		try {
			conn = getConnection();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet databases = dbm.getCatalogs();
			// Table exists
			if(databases.next()) {
				if (databases.getString(1).equals(databaseName)) return true;
			}
			return false;
		}
		catch(SQLException e) {
			LOG.error("", e);
		} finally {
			pool.releaseConnection(conn);
		}
		return false;
	}

	/**
	 * Creates a databse
	 * 
	 * @param database name
	 * @return
	 * @throws SQLException
	 */
	public boolean createDatabase(String databaseName) throws SQLException {
		return executeUpdate("CREATE DATABASE " + databaseName);
	}


	/**
	 * Returns the number of tables for a given postgreSQL database
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int countTables() throws SQLException {
		String countTablesQuery = "select count(*) from information_schema.tables;";
		ResultSet rs = executeQuery(countTablesQuery);
		rs.next();
		return rs.getInt(1);
	}

	/**
	 * @return the db
	 */
	public Database getDb() {
		return db;
	}

	/**
	 * @return the JDBC connection pool
	 */
	public SimpleJDBCConnectionPool getPool(){return pool;}

	/**
	 * Creates a table in the database for this given relation object. Returns true
	 * if the operation was successful.
	 * 
	 * @param relation
	 * @return
	 */
	public boolean createTable(Relation relation) {
		// If the relation already exists, no need to create it
		if(relation.exists()) {
			return false;
		}
		Map<String, PostgresAttType> types = new HashMap<String, PostgresAttType>();
		for(Attribute a: relation.getAttributes()) {
			types.put(a.getName(), a.getAttType());
		}
		String query;
		try {
			query = QueryHelper.getCreateTableQuery(relation, types);
		} catch (CSVFormatException e) {
			LOG.error("", e);
			return false;
		}
		return executeUpdate(query);
	}

	/**
	 * Populates the newRel with its shared attributes from sourceRel
	 * 	
	 * @param newRel
	 * @param sourceRel
	 * @return
	 */
	public boolean populateTable(Relation newRel, Relation sourceRel) {
		Set<Attribute> atts = newRel.getAttributes();
		// Build list of atts for insert query
		StringBuilder attList = new StringBuilder();
		Iterator<Attribute> iter = atts.iterator();
		attList.append(iter.next());
		while(iter.hasNext()) {
			attList.append(", " + iter.next());
		}

		String selectQuery = QueryHelper.getSelectQueryForAtts(sourceRel, atts);
		String insertQuery = null;
		insertQuery = String.format("INSERT INTO %s (%s) (%s)", newRel.getName(), attList, selectQuery);
		return executeUpdate(insertQuery);
	}

	/**
	 * Returns a Set of all of the attributes for a given table
	 * 
	 * @param rel
	 * @return
	 * @throws SQLException 
	 */
	public Set<Attribute> getRelationAttributes(Relation rel) {
		Set<Attribute> attrSet = new LinkedHashSet<Attribute>();
		Connection conn = null;
		try {
			conn = getConnection();
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getColumns(null, null, rel.getName().toLowerCase(), null);
			while(rs.next()) {
				String colName = rs.getString("COLUMN_NAME");
				String colType = rs.getString("TYPE_NAME");
				PostgresAttType type = PostgresAttType.valueOf(colType);

				Attribute attr = Attribute.existingAttribute(colName, type, rel);
				attrSet.add(attr);
			}
		} catch (SQLException e){
			LOG.error("", e);
		} finally {
			pool.releaseConnection(conn);
		}
		return attrSet;

	}

	/**
	 * Returns the primary key of the given relation according to the current
	 * database instance. Notably a primary key is a set of attributes which
	 * may hold 1 to n elements if it exists, and 0 elements if it does not.
	 * 
	 * @param relation
	 * @return
	 */
	public Set<Attribute> findPk(Relation relation) {
		// Add all primary keys to set of constraints
		Connection conn = null;
		try {
			conn = getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, relation.getName());
			Set<Attribute> attrs = relation.getAttributes();
			Set<Attribute> pk = new LinkedHashSet<Attribute>();
			while(primaryKeys.next()) {
				String pkName = primaryKeys.getString("COLUMN_NAME");
				String pkTable = primaryKeys.getString("TABLE_NAME");
				for(Attribute a: attrs) {
					if(a.getName().equalsIgnoreCase(pkName) 
							&& a.getSourceTable().getName().equalsIgnoreCase(pkTable)) {
						pk.add(a);
					}
				}
			}
			return pk;
		} catch(SQLException e) {
			LOG.error("", e);
			return null;
		} finally {
			if(conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	/**
	 * Returns all foreign keys that the relation contains, according to the
	 * current database instance
	 * 
	 * @param relation
	 * @return
	 */
	public Set<ForeignKey> findFks(Relation relation) {
		Set<Attribute> attrs = relation.getAttributes();
		Connection conn = null;
		Set<ForeignKey> fks = null;
		try {
			conn = getConnection();
			DatabaseMetaData metaData = conn.getMetaData();

			// Add all foreign keys to set to return
			fks = new LinkedHashSet<ForeignKey>();
			ResultSet foreignKeys = metaData.getImportedKeys(conn.getCatalog(), null, relation.getName().toLowerCase());
			while (foreignKeys.next()) {
				String fkTableName = foreignKeys.getString("FKTABLE_NAME");
				String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
				String pkTableName = foreignKeys.getString("PKTABLE_NAME");
				String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
				//				LOG.debug("{}.{} references {}.{}", fkTableName, fkColumnName, pkTableName, pkColumnName);
				for(Attribute a: attrs) {
					// If the foreign key is the same as some attribute in our relation
					// (i.e. they have the same column name and table name) then add that
					// foreign key constraint to the attribute in our relation
					if(a.getName().equalsIgnoreCase(fkColumnName) 
							&& a.getSourceTable().getName().equalsIgnoreCase(fkTableName)) {
						//LOG.debug("{} has fk {} referencing {}.{}", attrs, a, pkTableName, pkColumnName);
						Database db = a.getSourceTable().getSourceDb();
						PostgresAttType type = a.getAttType();
						Relation pkRel = RelationFactory.createExistingRelation(pkTableName, db);
						Attribute pk = Attribute.existingAttribute(pkColumnName, type, pkRel);
						Attribute fk = Attribute.existingAttribute(fkColumnName, type, relation);
						ForeignKey fkpk = ForeignKey.newInstance(fk, pk);
						fks.add(fkpk);
					}
				}
			}
			return fks;
		} catch(SQLException e) {
			LOG.error("", e);
			return null;
		} finally {
			pool.releaseConnection(conn);
		}
	}

	/**
	 * Deletes the passed relation from the database.
	 * 
	 * @param relation
	 */
	public void deleteTable(Relation relation) {
		String query = String.format("DROP TABLE %s;", relation.getName());
		executeUpdate(query);
	}

	/**
	 * Adds the passed attribute as a primary key of the passed relation r
	 * 
	 * @param primaryKey
	 * @param r
	 */
	public void addPrimaryKey(Attribute primaryKey, Relation r) {
		String query = String.format("ALTER TABLE %s ADD PRIMARY KEY(%s)", r.getName(), primaryKey.getName());
		executeUpdate(query);
	}

	/**
	 * Adds a constraint c to a given Attribute a in a relation r like
	 * a foreign key constraint s.t. a would reference c.pk
	 * 
	 * @param r
	 * @param a
	 * @param c
	 */
	public void addFk(Relation r, ForeignKey c) {
		String query = String.format("ALTER TABLE %s ADD FOREIGN KEY(%s) %s", r.getName(), c.getFk().getName(), c.asSql());
		executeUpdate(query);
	}

	/**
	 * Returns all violations of this soft fd in the given Relation paired with
	 * the proportion of the result that they occupy. In other words, the following
	 * data associated with this soft fd, a -> b
	 * a 	->		b
	 * Brandeis, Waltham
	 * Brandeis, Waltham
	 * Brandeis, Waltam
	 * 
	 * would return {Waltham -> 66.6, Waltam -> 33.3}
	 * 
	 * @return
	 */
	public Map<String, Map<String, Double>> getViolations(SoftFD softFD) {
		// Generate query for finding violations
		Map<String, Map<String, Double>> violations = new HashMap<String,Map<String, Double>>();
		String from = softFD.getFromAtt().getName();
		String to = softFD.getToAtt().getName();
		String rel = softFD.getFromAtt().getSourceTable().getName();
		String violationRelName = rel + "_fdviolations";
		// First store violating relations in temp table
		String query1 = String.format(
				"CREATE TEMP TABLE %s AS SELECT %s FROM (SELECT DISTINCT %s, %s FROM %s) AS foo GROUP BY %s HAVING COUNT(*) > 1;",
				violationRelName, from, from, to, rel, from);
		// Then join with that table to only get results for violating attributes
		String query2 = String.format("SELECT %s,%s,count(*) FROM %s natural join %s GROUP BY %s, %s;",
				from, to, rel, violationRelName, from, to);
		// Then make sure to drop the temp table
		String query3 = String.format("DROP TABLE %s;", violationRelName);
		ResultSet rs = null;
		try {
			// Create temp table
			executeUpdate(query1);
			// Query violating fds
			rs = executeQuery(query2);
			// Destroy temp table
			executeUpdate(query3);
		} catch (SQLException e) {
			LOG.error("", e);
			return violations;
		}

		// Iterate through results of query and build up violation map
		String violation = null;
		String fdLeft = null;
		try {
			while(rs.next()) {
				fdLeft = rs.getString(1);
				violation = rs.getString(2);
				int numViolations = rs.getInt(3);
				if(violations.containsKey(fdLeft)) {
					Map<String, Double> singleViolations = violations.get(fdLeft);
					singleViolations.put(violation,  Double.valueOf(numViolations));
				} else {
					Map<String, Double> singleViolations = new TreeMap<String, Double>();
					singleViolations.put(violation, Double.valueOf(numViolations));
					violations.put(fdLeft, singleViolations);
				}
			}
		} catch(SQLException e) {
			LOG.error("", e);
		}

		// Normalize the values of the map so that each is a percentage, rather than a count
		Set<String> determinants = violations.keySet();
		for(String d: determinants) {
			Set<String> determinedVals = violations.get(d).keySet();
			double total = 0.0;
			// First calculate the total count of possible vals for a given
			// determinant
			for(String possibleVal: determinedVals) {
				total += violations.get(d).get(possibleVal);
			}
			// Then turn each count into a percentage for each determinant
			for(String possibleVal: determinedVals) {
				double count = violations.get(d).get(possibleVal);
				double percentage = 100 * count / total;
				violations.get(d).put(possibleVal, percentage);
			}
		}
		// Now divide each entry by the total
		for(String d: determinants) {
			Set<String> determinedVals = violations.get(d).keySet();
			for(String possibleVal: determinedVals) {
				violations.get(d).get(possibleVal);
			}
		}
		return violations;
	}

	/**
	 * Applies all passed corrections to the softFd by ensuring that each key
	 * of corrections<key, value> only corresponds with value in the database
	 * 
	 * @param softFd
	 * @param key
	 * @param correctedVal
	 */
	public void fixAllViolations(SoftFD softFd, Map<String, String> corrections) {
		for(String s: corrections.keySet()) {
			fixSingleViolation(softFd, s, corrections.get(s));
		}
		// Make sure that source relation now knows that softFd is actually a hard fd
		softFd.getFromAtt().getSourceTable().addFd(softFd);
	}

	/**
	 * Fixes a single instance of a violated soft functional dependency key -> correctVal
	 * within the database where correctVal is the only value that should be associated
	 * with key
	 * 
	 * @param softFd
	 * @param determinant
	 * @param correctVal
	 */
	private void fixSingleViolation(SoftFD softFd, String determinant, String correctVal) {
		// Side of the functional dependency that we're going to fix
		Attribute leftSide = softFd.getFromAtt();
		Attribute attToFix = softFd.getToAtt();
		if(attToFix.getAttType().isCharType()) {
			determinant = String.format("'%s'", determinant);
			correctVal = String.format("'%s'", correctVal);
		}
		// Relation of that functional dependency
		Relation rel = softFd.getFromAtt().getSourceTable();
		// Query to correct the old value to the new
		String update = String.format("UPDATE %s SET %s=%s where %s=%s;",
				rel.getName(), attToFix.getName(), correctVal, leftSide.getName(), determinant);
		executeUpdate(update);
	}
}

