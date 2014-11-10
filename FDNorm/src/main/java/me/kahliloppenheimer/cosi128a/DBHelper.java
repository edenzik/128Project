package me.kahliloppenheimer.cosi128a;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class to abstract away from boilerplate of JDBC connection
 * 
 * @author kahliloppenheimer
 *
 */
public class DBHelper {
	
	private Statement stmt;
	private Connection conn;
	
	public DBHelper(String hostIP, String dbName, String dbUser, String dbPass) throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://" + hostIP + "/" + dbName;
		System.out.println("url = " + url);
		conn = DriverManager.getConnection(url, dbUser, dbPass);
		stmt = conn.createStatement();
	}
	
	/**
	 * Executes the given update query (i.e. "insert into foo values ("bar");")
	 * 
	 * @param updateQuery update Query to be executed
	 * @throws SQLException
	 */
	public void executeUpdate(String updateQuery) {
		try {
			stmt.executeUpdate(updateQuery);
			System.out.println("SUCCESSFUL UPDATE");
		} catch(SQLException e) {
			System.out.println("Bad query: " + updateQuery);
			// just means table already exists (probably)
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
		return stmt.executeQuery(query);
	}
	
	/**
	 * Closes the connection to the given database
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		conn.close();
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

}

