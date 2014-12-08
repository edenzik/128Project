/**
 * 
 */
package com.wrangler.ui.upload;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Upload.ChangeEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.wrangler.ui.callback.Callback;
import com.wrangler.ui.login.User;
import com.wrangler.ui.wranglertool.WranglerWindow;

/**
 * @author edenzik
 *
 */
public abstract class UploadWindow extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final CSVUpload uploader;
	protected final CheckBox hasHeaders;
	protected String tableName = ""; 

	/**
	 * This is a superclass of all upload windows, whether wrangler or otherwise.
	 * 
	 * 
	 */
	public UploadWindow(final UI ui, final User user, final Callback callback) {
		setCaption("Upload CSV here");
		setDraggable(false);
		setResizable(false);
		setHeight("25%");
		setWidth("30%");
		center();
		VerticalLayout layout = new VerticalLayout();
		
		layout.setMargin(true);
		uploader = new CSVUpload();
		layout.addComponent(uploader);
		final TextField name = new TextField("Table Name");
		//layout.addComponent(name);
		
		hasHeaders = new CheckBox("Has headers?", true);
		//layout.addComponent(hasHeaders);
		hasHeaders.setVisible(false);
		setContent(layout);
		HorizontalLayout nameHeaders = new HorizontalLayout(name, hasHeaders);
		layout.addComponent(nameHeaders);
		layout.addComponent(name);
		uploader.addChangeListener(new Upload.ChangeListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void filenameChanged(ChangeEvent event) {
				String[] fileNameDelimted = event.getFilename().split("\\\\");
				String fileName = fileNameDelimted[fileNameDelimted.length-1].split(".")[0];
				name.setValue(fileName);
				
			}
		});
		uploader.addFinishedListener(new Upload.FinishedListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void uploadFinished(FinishedEvent event) {
				tableName = name.getValue();
				close();
			}
		});
	}

}
