package me.kahliloppenheimer.cosi128a;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOG = LoggerFactory.getLogger(WranglerServer.class);
	
    public static void main(String[] args) throws Exception {
    	// initialize dbHelper
    	dbHelper = new DBHelper(HOST_IP, DB_NAME, USER, PASS);
        // initialize server
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new NewHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        LOG.info("Wrangler Server successfully started!");
    }

    // Current handler of HTTP requests (i.e. transforming HTTP requests into
    // SQL commands and executing them
    static class NewHandler implements HttpHandler {

		public void handle(HttpExchange t) throws IOException {
			// TODO: Insert some sort of check here to make sure that the http request
			// is to put newly rangled data into the db
			if(true) {
				RangledData rd = new RangledData(t, dbHelper);
				rd.createAndPopulateInitialTable();
			}
		} 
    }
}
