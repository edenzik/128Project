package com.wrangler.ui.upload;

import java.io.IOException;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.wrangler.extract.WrangledDataExtractor;
import com.wrangler.ui.callback.Callback;
import com.wrangler.ui.login.User;

public class DirectUploadWindow extends UploadWindow {

	public DirectUploadWindow(final UI ui, final User user, final Callback callback) {
		super(ui, user, callback);
		hasHeaders.setVisible(true);
		uploader.addFinishedListener(new Upload.FinishedListener() {
			@Override
			public void uploadFinished(FinishedEvent event) {
				WrangledDataExtractor wde;
				try {
					String csvFile = uploader.getOutputStream().toString();
					if (!hasHeaders.getValue()){
						StringBuilder headerLine = new StringBuilder();
						for (int i = 0; i < csvFile.substring(0, csvFile.indexOf("\n")).split(",").length; i++){
							headerLine.append("attribute" + i + ",");
						}
						headerLine.deleteCharAt(headerLine.length()-1);
						csvFile = headerLine.toString() + "\n" + csvFile;
					}
					wde = new WrangledDataExtractor(csvFile, user.getDB());
					wde.createAndPopulateInitialTable();
					Notification.show("Sucesss",
			                  "CSV has been uploaded",
			                  Notification.Type.TRAY_NOTIFICATION);
					callback.execute();
				} catch (IOException e) {
					Notification.show("Oops",
			                  "Something went wrong.",
			                  Notification.Type.ERROR_MESSAGE);
				} catch (AssertionError e) {
					Notification.show("Oops",
			                  "Does your file have headers?",
			                  Notification.Type.ERROR_MESSAGE);
				}
			}
		});
	}

}
