package me.kahliloppenheimer.cosi128a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class WranglerServer {
	
	private static final String HOST_IP = "104.236.17.70";
	private static final String DB_NAME = "cosi128a";
	private static final String USER = "kahlil";
	private static final String PASS = "psswd";
	private static DBHelper dbHelper; 
	
    public static void main(String[] args) throws Exception {
    	// initialize dbHelper
    	dbHelper = new DBHelper(HOST_IP, DB_NAME, USER, PASS);
        // initialize server
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new NewHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    // Current handler of HTTP requests (i.e. transforming HTTP requests into
    // SQL commands and executing them
    static class NewHandler implements HttpHandler {

		public void handle(HttpExchange t) throws IOException {
			BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
			String firstLine = br.readLine();
			if(firstLine != null) {
				String[] headers = parseLine(firstLine);
				// TODO: Make this guess at anticipated read chars more sophisticated
				br.mark(QueryHelper.TYPE_INFERENCE_SAMPLE_SIZE * headers.length * 100);
				Map<String, List<String>> headersToSampleValues = getSampleValues(headers, br);
				br.reset();
				// Make the table name by appending the current number of tables to the
				// default table name
				String tableName = null;
				try {
					tableName = QueryHelper.DEFAULT_TABLE_NAME + dbHelper.countTables();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new Error("Could not count tables!");
				}
				Map<String, PostgresAttType> inferredTypes = QueryHelper.inferTableTypes(headers, headersToSampleValues);
				String createTableQuery = QueryHelper.getCreateTableQuery(tableName, inferredTypes);
				dbHelper.executeUpdate(createTableQuery);
				String nextLine = null;
				while((nextLine = br.readLine()) != null) {
					String[] atts = parseLine(nextLine);
					String insertQuery = QueryHelper.getInsertQuery(inferredTypes, atts, tableName);
					dbHelper.executeUpdate(insertQuery);
				}
			} else {
				throw new Error("Empty CSV File!");
			}
		} 

		// Given a string of headers and a buffered reader positioned at the start of all of the values
		// returns a mapping of headers to sample values (i.e. name -> {"John", "Jacob", "Jingleheimer"})
		private Map<String, List<String>> getSampleValues(String[] headers, BufferedReader br) throws IOException {
			Map<String, List<String>> headerValueMap = new HashMap<String, List<String>>();
			String nextLine = null;
			int lineCount = 0;
			while((nextLine = br.readLine()) != null && lineCount < QueryHelper.TYPE_INFERENCE_SAMPLE_SIZE) {
				++lineCount;
				String[] parsedLine = parseLine(nextLine);
				for(int i = 0; i < parsedLine.length; ++i) {
					String key = headers[i];
					String value = parsedLine[i];
					// If list already exists, simply append value to it
					if(headerValueMap.containsKey(key)) {
						headerValueMap.get(key).add(value);
					}
					// Otherwise, create new list with the first value
					else {
						// Notable the size should be of the default sample size for type inference
						List<String> valueList = new ArrayList<String>(QueryHelper.TYPE_INFERENCE_SAMPLE_SIZE);
						valueList.add(value);
						headerValueMap.put(key, valueList);
					}
				}
			}
			return headerValueMap;
		}

		/**
		 * Returns a string array of all entries from one line of CSV
		 * 
		 * @param line
		 * @return
		 */
		private String[] parseLine(String line) {
			String[] splitLine =  line.split(",");
			for(int i = 0; i < splitLine.length; ++i) {
				splitLine[i] = splitLine[i].trim();
			}
			return splitLine;
		}
    }
    
//    // Used for original testing, but no longer useful
//    static class OldHandler implements HttpHandler {
//        public void handle(HttpExchange t) throws IOException {
//        	System.out.println("POST REQUEST BODY:\n");
//        	BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
//        	String nextLine = null;
//        	while((nextLine = br.readLine()) != null) {
//        		String[] args = nextLine.split("&");
//        		String name = args[0].split("=")[1].trim();
//        		int age = Integer.parseInt(args[1].split("=")[1].trim());
//        		System.out.println(Arrays.toString(args));
//        		insert(name, age);
//        	}
//        	System.out.println();
//            String response = "This is the response";
//         
//            t.sendResponseHeaders(200, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        }
//        
//        void insert(String name, int age) {
//        	dbHelper.executeUpdate("insert into person (name, age) values ('" + name + "', " + age + ");");
//        }
//    }

}
