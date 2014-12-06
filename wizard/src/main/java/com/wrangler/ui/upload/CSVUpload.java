/**
 * 
 */
package com.wrangler.ui.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

/**
 * @author edenzik
 *
 */
public class CSVUpload extends Upload {
	private final OutputStream csv;
	public CSVUpload() {
		csv = new ByteArrayOutputStream();
		setReceiver(new Receiver(){
			public OutputStream receiveUpload(String filename,String mimeType) {
				return csv;
			}
		});
		VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {
			@Override
			public boolean handleRequest(VaadinSession session,
					VaadinRequest request,
					VaadinResponse response)
							throws IOException {
				if ("/csvUpload".equals(request.getPathInfo())) {
					response.setContentType("text/plain");
					response.getWriter().append(getOutputStream().toString());
					return true;
				} else {
					return false;
				}
			}
		});
	}
	
	public OutputStream getOutputStream(){return csv;}

}
