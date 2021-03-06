/**
 * 
 */
package com.wrangler.ui.wranglertool;

import java.io.IOException;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.UI;
import com.wrangler.extract.WrangledDataExtractor;
import com.wrangler.ui.login.User;

/**
 * @author edenzik
 *
 */
public class DataWrangler extends BrowserFrame {


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
	DataWrangler(UI ui, final User user, final String tableName) {
		super("", new ExternalResource(URI));

		RequestHandler handler = new RequestHandler(){
			private static final long serialVersionUID = -5193766318144593205L;
			private StringBuffer result = new StringBuffer();
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
					WrangledDataExtractor wde = new WrangledDataExtractor(result.toString(), user.getDB());
					wde.createAndPopulateInitialTable(tableName);
					done = true;
					return true;
				} else return false;
			}
		};
		VaadinSession.getCurrent().addRequestHandler(handler);
	}

	void setReady(){ready = true;}

	boolean isDone(){return done;}
}
