package com.wrangler.load;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to abstract away from boilerplate of JDBC connection
 * 
 * @author kahliloppenheimer
 *
 */
public class DBHelper {

	private Statement stmt;
	private Connection conn;
	private final Logger LOG = LoggerFactory.getLogger(DBHelper.class);
	private final Database db;

	
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
		String url = "jdbc:postgresql://" + db.getHost().getIp() + "/" + db.getDbName();
		conn = DriverManager.getConnection(url, db.getHost().getRole(), db.getHost().getPass());
		LOG.info("Connected to database at {}", url);
		stmt = conn.createStatement();
	}

	/**
	 * Executes the given update query (i.e. "insert into foo values ("bar");")
	 * 
	 * @param updateQuery update Query to be executed
	 * @throws SQLException
	 */
	public synchronized void executeUpdate(String query) {
		try {
			stmt.executeUpdate(query);
			LOG.info(query);
		} catch(SQLException e) {
			LOG.info(query);
//			LOG.error(updateQuery);
//			LOG.error("", e);
		}
	}

	/**
	 * Executes the given select query (i.e. "select * from foo;")
	 * 
	 * @param query the query to be executed
	 * @return
	 * @throws SQLException
	 */
	public synchronized ResultSet executeQuery(String query) {
		LOG.info(query);
		try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			LOG.error(query);
//			LOG.error("", e);
			return null;
		}
	}
	
	/**
	 * Checks if the result of this query exists
	 * 
	 * @param if the result of this query is empty or not (true if not empty)
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean exists(String query) {
		LOG.info(query);
		try {
			return stmt.executeQuery(query).next();
		} catch (SQLException e) {
			LOG.error(query);
			return false;
		}
	}

	/**
	 * Closes the connection to the given database
	 * 
	 * @throws SQLException
	 */
	public synchronized void close() throws SQLException {
		LOG.info("Closed database connection!");
		conn.close();
	}
	
	

	/**
	 * Returns true if a given table exists and false if it does not
	 * 
	 * @param rel
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean tableExists(Relation rel) {
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, rel.getName().toLowerCase(), null);
			// Table exists
			if(tables.next()) {
				return true;
			}
		}
		catch(SQLException e) {
			LOG.error("Could not check if table {} exists or not!", rel, e);
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
	public synchronized boolean databaseExists(String databaseName) {
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet databases = dbm.getCatalogs();
			// Table exists
			if(databases.next()) {
				if (databases.getString(1).equals(databaseName)) return true;
			}
		}
		catch(SQLException e) {
			LOG.error("Could not check if database {} exists or not!", databaseName, e);
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
	public synchronized boolean createDatabase(String databaseName) {
		return executeQuery("CREATE DATABASE " + databaseName)!=null;
	}


	/**
	 * Returns the number of tables for a given postgreSQL database
	 * 
	 * @return
	 * @throws SQLException
	 */
	public synchronized int countTables() throws SQLException {
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
	 * Used only for unit testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database db = DatabaseFactory.createDatabase("kahliloppenheimer", HostFactory.createDefaultHost());
			Relation rel = RelationFactory.createRelation("table154", db);
			Set<Attribute> attrs = db.getDbHelper().getTableAttributes(rel);
			System.out.println(attrs);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns a Set of all of the attributes for a given table
	 * 
	 * @param rel
	 * @return
	 * @throws SQLException 
	 */
	public Set<Attribute> getTableAttributes(Relation rel) throws SQLException {
		Set<Attribute> attrSet = new LinkedHashSet<Attribute>();

		DatabaseMetaData meta = conn.getMetaData();
		ResultSet rs = meta.getColumns(null, null, rel.getName().toLowerCase(), null);
		while(rs.next()) {
			String colName = rs.getString("COLUMN_NAME");
			String colType = rs.getString("TYPE_NAME");
			Attribute attr = AttributeFactory.createAttribute(colName, colType, rel);
			attrSet.add(attr);
		}

		return attrSet;
		
	}
	
}

