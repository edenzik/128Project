package me.kahliloppenheimer.cosi128a;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class ServerTest {
	
	private static String hostIP = "104.236.17.70";
	private static String dbName = "cosi128a";
	private static String user = "kahlil";
	private static String pass = "psswd";
	private static DBHelper dbHelper; 
	
	private static final String CREATE_PERSON_TABLE = "create table person (name varchar(20), age int);";

    public static void main(String[] args) throws Exception {
    	dbHelper = new DBHelper(hostIP, dbName, user, pass);
		dbHelper.executeUpdate(CREATE_PERSON_TABLE);
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new NewHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class NewHandler implements HttpHandler {

		public void handle(HttpExchange t) throws IOException {
			BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
			
			String tmp = br.readLine();
			if(tmp != null) {
				String[] headers = parseLine(tmp);
				String createTableQuery = createTableQuery("people", headers);
				dbHelper.executeUpdate(createTableQuery);
				String nextLine = null;
				while((nextLine = br.readLine()) != null) {
					String[] atts = parseLine(nextLine);
					String insertQuery = getInsertQuery(atts);
					dbHelper.executeUpdate(insertQuery);
				}
			} else {
				throw new Error("Empty CSV File!");
			}
		} 
		
		/**
		 * Returns a string array of all entries from one line of CSV
		 * 
		 * @param line
		 * @return
		 */
		private String[] parseLine(String line) {
			return line.split(",");
		}
		
		/**
		 * Returns a create table command with the passed table name and
		 * String array of schema attributes
		 * 
		 * @param name
		 * @param atts
		 * @return
		 */
		private String createTableQuery(String name, String[] atts) {
			if(atts.length == 0) {
				throw new Error("Creating table with no attributes");
			}
			final String ATT_TYPE = " varchar(100)";
			StringBuilder attList = new StringBuilder();
			attList.append("(");
			attList.append(atts[0] + ATT_TYPE);
			for(int i = 1; i < atts.length; ++i) {
				attList.append(", " + atts[i] + ATT_TYPE);
			}
			attList.append(")");
			String query = "CREATE TABLE " + name + attList.toString();
			System.out.println("update query = " + query);
			return query;
		}
		
		/**
		 * Returns a String representing an insert query with the given values
		 * 
		 * @param values
		 * @return
		 */
		private String getInsertQuery(String[] values) {
			if(values.length == 0) {
				throw new Error("Inserting 0 values error!");
			}
			StringBuilder valueList = new StringBuilder();
			valueList.append("(");
			valueList.append(values[0]);
			for(int i = 1; i < values.length; ++i) {
				valueList.append(", " + values[i]);
			}
			valueList.append(")");
			String query = "INSERT INTO people VALUES " + valueList.toString();
			return query;
		}
    	
    }
    
    static class OldHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	System.out.println("POST REQUEST BODY:\n");
        	BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
        	String nextLine = null;
        	while((nextLine = br.readLine()) != null) {
        		String[] args = nextLine.split("&");
        		String name = args[0].split("=")[1].trim();
        		int age = Integer.parseInt(args[1].split("=")[1].trim());
        		System.out.println(Arrays.toString(args));
        		insert(name, age);
        	}
        	System.out.println();
            String response = "This is the response";
         
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        void insert(String name, int age) {
        	dbHelper.executeUpdate("insert into person (name, age) values ('" + name + "', " + age + ");");
        }
    }

}
