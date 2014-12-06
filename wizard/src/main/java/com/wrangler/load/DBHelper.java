package com.wrangler.load;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;

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
	 * Executes the given update query (i.e. "insert into foo values ("bar");")
	 * 
	 * @param updateQuery update Query to be executed
	 * @throws SQLException
	 */
	public void executeUpdate(String query) {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			LOG.info("{}: {}", getDb(), query);
		} catch(SQLException e) {
			LOG.error("", e);
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
				Relation rel = RelationFactory.createRelation(tableName, getDb());
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
		return executeQuery("CREATE DATABASE " + databaseName)!=null;
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
	 * Used only for unit testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database db = DatabaseFactory.createDatabase("kahliloppenheimer", HostFactory.createDefaultHost());
			Relation rel = RelationFactory.createRelation("table154", db);
			System.out.println(db.getDbHelper().getRelations());
			Set<Attribute> attrs = db.getDbHelper().getRelationAttributes(rel);
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
	public Set<Attribute> getRelationAttributes(Relation rel) {
		Set<Attribute> attrSet = new LinkedHashSet<Attribute>();
		try {
			Connection conn = getConnection();
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getColumns(null, null, rel.getName().toLowerCase(), null);
			while(rs.next()) {
				String colName = rs.getString("COLUMN_NAME");
				String colType = rs.getString("TYPE_NAME");
				Attribute attr = AttributeFactory.createAttribute(colName, colType, rel);
				attrSet.add(attr);
			}
		} catch (SQLException e){
			LOG.error("", e);
		}

		return attrSet;

	}

	/**
	 * @return the JDBC connection pool
	 */
	public SimpleJDBCConnectionPool getPool(){return pool;}

}

