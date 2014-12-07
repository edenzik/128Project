/**
 * 
 */
package com.wrangler.ui.upload;

import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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
	protected final CSVUpload uploader;

	/**
	 * 
	 * 
	 */
	public UploadWindow(final UI ui, final User user, final Callback callback) {
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
		setContent(layout);
		uploader.addFinishedListener(new Upload.FinishedListener() {
			@Override
			public void uploadFinished(FinishedEvent event) {
				close();
			}
		});
	}

}
