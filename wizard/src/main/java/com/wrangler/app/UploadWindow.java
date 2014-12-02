/**
 * 
 */
package com.wrangler.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window.CloseEvent;

/**
 * @author edenzik
 *
 */
class UploadWindow extends Window {
	private final CSVUpload uploader;

	/**
	 * 
	 */
	public UploadWindow() {
		setCaption("Upload CSV here");
		setDraggable(false);
		setResizable(false);
		setHeight("20%");
		setWidth("30%");
		center();
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		uploader = new CSVUpload();
		layout.addComponent(uploader);
		uploader.addFinishedListener(new Upload.FinishedListener() {
			@Override
			public void uploadFinished(FinishedEvent event) {close();}});
		setContent(layout);
	}
	
	CSVUpload getUploader(){return uploader;}

}
