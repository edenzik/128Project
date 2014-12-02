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
		String url = "jdbc:postgresql://" + db.getHostIp() + "/" + db.getDbName();
		conn = DriverManager.getConnection(url, db.getUser(), db.getPass());
		LOG.info("Connected to database at {}", url);
		stmt = conn.createStatement();
	}

	/**
	 * Creates a DB helper object with passed fields, rather than by constructing
	 * Database object outside of the method.
	 * 
	 * @param hostIP
	 * @param dbName
	 * @param dbUser
	 * @param dbPass
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public DBHelper(String hostIP, String dbName, String dbUser, String dbPass) throws ClassNotFoundException, SQLException {
		this(new Database(hostIP, dbName, dbUser, dbPass));
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
	 * Returns true if the passed user_name exists in the given db
	 * 
	 * @param user_name
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean userExists(String user_name) throws ClassNotFoundException, SQLException{
		return exists("SELECT * FROM users WHERE email=" + "'" + user_name + "'");
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
	 * Creates a new user
	 * 
	 * @param user to be created, their password
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean addUser(String userName, String userPassword) {
		executeUpdate("INSERT INTO users VALUES('" +  userName + "', '" + userPassword + "')");
		return true;
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
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://104.236.17.70/cosi128a";
			System.out.println("CONNECTING TO DATABASE");
			conn = DriverManager.getConnection(url, "kahlil", "psswd");
			Statement stmt = conn.createStatement();
			System.out.println("CREATING TABLE TEST");
			try {
				stmt.executeUpdate("Create table test (name int);");
			} catch(SQLException e) {
				// just means table already exists
			}
			System.out.println("POPULATING TABLE TEST");
			for(int i = 0; i < 2000; ++i) {
				stmt.executeUpdate("insert into test (name) values (" + i + ");");
			}
			System.out.println("QUERYING TABLE TEST");
			ResultSet rs = stmt.executeQuery("select * from test;");
			while(rs.next()) {
				int name = rs.getInt("name");
				System.out.println(name);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
//			String colName = rs.getString("COLUMN_NAME");
//			String colType = rs.getString("TYPE_NAME");
			
			//TODO: convert from string colType from DB to my enum colType

			//Attribute attr = new Attribute(colName, colType, rel);
		}

		return attrSet;
		
	}

}

