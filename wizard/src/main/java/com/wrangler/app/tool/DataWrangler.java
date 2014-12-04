/**
 * 
 */
package com.wrangler.app.tool;

import java.io.IOException;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.BrowserFrame;

/**
 * @author edenzik
 *
 */
public class DataWrangler extends BrowserFrame {

	private StringBuilder result = new StringBuilder();
	private boolean read = false;
	private boolean done = false;
	private static final String URI = "/VAADIN/wrangler/index.html";

	/**
	 * 
	 */
	public DataWrangler() {
		super("", new ExternalResource(URI));
		RequestHandler handler = new RequestHandler(){
			private static final long serialVersionUID = -5193766318144593205L;

			public boolean handleRequest(VaadinSession session,
					VaadinRequest request,
					VaadinResponse response)
							throws IOException {
				if ("/isDone".equals(request.getPathInfo())) {
					response.setContentType("text/plain");
					if (read) response.getWriter().append("TRUE");
					else response.getWriter().append("FALSE");
					return true; // We wrote a response
				} if ("/allDone".equals(request.getPathInfo())) {
					String item = request.getParameter("CHART_VALUE");
					System.out.println(item);
					if (item.contains((Long.toString(serialVersionUID)))) done = true;
					result.append(item);
					return true; // We wrote a response
				} else return false;
			}
		};
		VaadinSession.getCurrent().addRequestHandler(handler);
	}

	public StringBuilder getResult(){return result;}
	
	void setRead(){read = true;}
	
	boolean isDone(){return done;}
}
