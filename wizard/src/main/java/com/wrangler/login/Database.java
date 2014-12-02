package com.wrangler.login;

import java.sql.SQLException;

import com.wrangler.load.DBHelper;

public class Database {
	private final String DB_IP;
	private final String DB_PORT;
	private final String DB_NAME; 
	
	private static final String HOST_IP = "104.236.17.70";
	private static final String HOST_PORT = "5432";
	private static final String HOST_DB_NAME = "default";
	private static final String HOST_DB_USER = "kahlil";
	private static final String HOST_DB_PASS = "psswd";
	
	public Database() throws ClassNotFoundException, SQLException{
		this("default");
	}
	
	public Database(String db_name) throws ClassNotFoundException, SQLException{
		this(db_name, "104.236.17.70", "5432");
	}
	
	private Database(String db_name, String db_ip, String db_port) throws ClassNotFoundException, SQLException{
		DB_NAME = db_name;
		DB_IP = db_ip;
		DB_PORT = db_port;
		initialize();
	}
	
	public String getIP(){return DB_IP;}
	public String getPort(){return DB_PORT;}
	public String getName(){return DB_NAME;}
	
	boolean initialize() throws ClassNotFoundException, SQLException{
		DBHelper db = new DBHelper(HOST_IP, HOST_DB_NAME, HOST_DB_USER, HOST_DB_PASS);
		if (!db.databaseExists(getName())) {
			return db.createDatabase(getName());
		}
		return true;
	}
	
	static boolean addUser(String user_name, String user_password) throws ClassNotFoundException, SQLException{
		DBHelper db = new DBHelper(HOST_IP, HOST_DB_NAME, HOST_DB_USER, HOST_DB_PASS);
		db.addUser(user_name, user_password);
		return false;
	}
	
	static boolean userExists(String user_name) throws ClassNotFoundException, SQLException{
		DBHelper db = new DBHelper(HOST_IP, HOST_DB_NAME, HOST_DB_USER, HOST_DB_PASS);
		return db.exists("SELECT * FROM users WHERE email=" + "'" + user_name + "'");
	}
	
	
}
