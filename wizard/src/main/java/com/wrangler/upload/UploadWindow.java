/**
 * 
 */
package com.wrangler.upload;

import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

/**
 * @author edenzik
 *
 */
public class UploadWindow extends Window {
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
	
	public CSVUpload getUploader(){return uploader;}

}
