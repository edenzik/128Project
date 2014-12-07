package com.wrangler.load;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.wrangler.extract.CSVFormatException;
import com.wrangler.normalization.Normalizer;

/**
 * Class to abstract away from boilerplate of JDBC connection
 * 
 * @author kahliloppenheimer
 *
 */
public class DBHelper {

	private SimpleJDBCConnectionPool pool;
	private final Logger LOG = LoggerFactory.getLogger(DBHelper.class);
	private final Database db;

	/**
	 * Used only for unit testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database db = DatabaseFactory.createDatabase("kahliloppenheimer", HostFactory.createDefaultHost());
			Relation rel = RelationFactory.createExistingRelation("table152", db);
			Normalizer norm = Normalizer.newInstance(rel);
			Set<Relation> normalized = norm.bcnf();
			System.out.println("NORMALIZED = " + normalized);
			rel.decomposeInto(normalized);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	}

	/**
	 * Returns a new Connection from the Connection pool that
	 * auto-commits
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		Connection conn = pool.reserveConnection();
		conn.setAutoCommit(true);
		return conn;
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
		String selectQuery = QueryHelper.getSelectQueryForAtts(sourceRel, atts);
		String pk = newRel.getPrimaryKey().getName();
		String insertQuery = null;
		if(pk != null) {
			insertQuery = String.format("INSERT INTO %s (%s) GROUP BY %s;", 
					newRel.getName(), selectQuery, newRel);
		} else {
			insertQuery = String.format("INSERT INTO %s (%s)", newRel.getName(), selectQuery);
		}
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
		try {
			Connection conn = getConnection();
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getColumns(null, null, rel.getName().toLowerCase(), null);
			while(rs.next()) {
				String colName = rs.getString("COLUMN_NAME");
				String colType = rs.getString("TYPE_NAME");
				PostgresAttType type = PostgresAttType.valueOf(colType);

				Attribute attr = Attribute.withoutConstraints(colName, type, rel);
				attrSet.add(attr);
			}
		} catch (SQLException e){
			LOG.error("", e);
		}
		return attrSet;

	}

	/**
	 * Checks and updates all constraints for all attributes of the passed
	 * relation so that each attribute will now store any constraints that
	 * it has
	 * 
	 * @param relation
	 * @return
	 */
	protected void findAndSetConstraints(Relation relation) {
		Set<Attribute> attrs = relation.getAttributes();
		Connection conn = null;
		try {
			conn = getConnection();
			DatabaseMetaData metaData = conn.getMetaData();

			// Add all primary keys to set of constraints
			ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, relation.getName());
			int counter = 0;
			while(primaryKeys.next()) {
				String pkName = primaryKeys.getString("COLUMN_NAME");
				String pkTable = primaryKeys.getString("TABLE_NAME");
				for(Attribute a: attrs ) {
					if(a.getName().equalsIgnoreCase(pkName) 
							&& a.getSourceTable().getName().equalsIgnoreCase(pkTable)) {
						relation.setPrimaryKey(a);
					}
				}
				counter++;
				if(counter > 1) {
					throw new AssertionError("Not supporting multiple primary keys!");
				}
			}

			// Add all foreign keys to set of constraints
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
						//						LOG.debug("{} has fk {} referencing {}.{}", attrs, a, pkTableName, pkColumnName);
						Database db = a.getSourceTable().getSourceDb();
						PostgresAttType type = a.getAttType();
						Relation pkRel = RelationFactory.createExistingRelation(pkTableName, db);
						Attribute pk = Attribute.withoutConstraints(pkColumnName, type, pkRel);
						a.addFK(pk);
					}
				}
			}
		} catch(SQLException e) {
			LOG.error("", e);
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
	public void addConstraint(Relation r, Attribute a, Constraint c) {
		String query = String.format("ALTER TABLE %s ADD FOREIGN KEY(%s) %s", r.getName(), a.getName(), c.asSql());
		executeUpdate(query);
	}
}

