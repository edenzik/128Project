package com.wrangler.ui.upload;

import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.wrangler.ui.callback.Callback;
import com.wrangler.ui.login.User;
import com.wrangler.ui.wranglertool.WranglerWindow;

public class WranglerUploadWindow extends UploadWindow {

	public WranglerUploadWindow(final UI ui, final User user, final Callback callback) {
		super(ui, user, callback);
		uploader.addFinishedListener(new Upload.FinishedListener() {
			@Override
			public void uploadFinished(FinishedEvent event) {
				WranglerWindow wrangler = new WranglerWindow(ui, user, callback, tableName);
				ui.addWindow(wrangler);
			}
		});
	}

}
