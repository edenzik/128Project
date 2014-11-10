package me.kahliloppenheimer.cosi128a;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

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

    public static void main(String[] args) throws Exception {
    	dbHelper = new DBHelper(hostIP, dbName, user, pass);
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	System.out.println("POST REQUEST BODY:\n");
        	BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
        	String nextLine = null;
        	while((nextLine = br.readLine()) != null) {
        		System.out.println(nextLine);
        	}
        	System.out.println();
            String response = "This is the response";
         
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
