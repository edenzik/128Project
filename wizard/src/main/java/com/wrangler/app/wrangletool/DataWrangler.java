/**
 * 
 */
package com.wrangler.app.wrangletool;

import java.io.IOException;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.BrowserFrame;
import com.wrangler.load.Database;

/**
 * @author edenzik
 *
 */
public class DataWrangler extends BrowserFrame {

	private StringBuffer result = new StringBuffer();
	private boolean ready = false;
	private boolean done = false;
	private static final String URI = "/VAADIN/wrangler/index.html";

	/**
	 * This is the embedded Browser Window of data Wrangler
	 * Serves as a listener for requests - using GET and POST to communicate with
	 * the front end.
	 * isReady -> Backend issues a POST to front end "TRUE"
	 * sendData -> Front end sends CSV data in chunks in response to that address
	 * allDone -> Front end indicates its done submitting
	 * 
	 */
	DataWrangler() {
		super("", new ExternalResource(URI));
		
		RequestHandler handler = new RequestHandler(){
			private static final long serialVersionUID = -5193766318144593205L;

			public boolean handleRequest(VaadinSession session,
					VaadinRequest request,
					VaadinResponse response)
							throws IOException {
				if ("/isReady".equals(request.getPathInfo())) {
					response.setContentType("text/plain");
					if (ready) response.getWriter().append("TRUE");
					else response.getWriter().append("FALSE");
					return true;
				} if ("/sendData".equals(request.getPathInfo())) {
					result.append(request.getParameter("CHART_VALUE"));
					return true;
				} if ("/allDone".equals(request.getPathInfo())) {
					done = true;
					return true;
				} else return false;
			}
		};
		VaadinSession.getCurrent().addRequestHandler(handler);
	}

	public StringBuffer getResult(){return result;}
	
	void setReady(){ready = true;}
	
	boolean isDone(){return done;}
}
