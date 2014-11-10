package me.kahliloppenheimer.cosi128a;

import java.sql.*;

public class DBTest {

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

