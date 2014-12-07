package com.wrangler.ui.upload;

import java.io.IOException;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.wrangler.extract.WrangledDataExtractor;
import com.wrangler.ui.callback.Callback;
import com.wrangler.ui.login.User;
import com.wrangler.ui.wranglertool.WranglerWindow;

public class DirectUploadWindow extends UploadWindow {

	public DirectUploadWindow(final UI ui, final User user, final Callback callback) {
		super(ui, user, callback);
		uploader.addFinishedListener(new Upload.FinishedListener() {
			@Override
			public void uploadFinished(FinishedEvent event) {
				WrangledDataExtractor wde;
				try {
					wde = new WrangledDataExtractor(uploader.getOutputStream().toString(), user.getDB());
					wde.createAndPopulateInitialTable();
					Notification.show("Sucesss",
			                  "CSV has been uploaded",
			                  Notification.Type.TRAY_NOTIFICATION);
					callback.execute();
				} catch (IOException e) {
					Notification.show("Oops",
			                  "Something went wrong.",
			                  Notification.Type.ERROR_MESSAGE);
				}
			}
		});
	}

}
