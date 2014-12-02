/**
 * 
 */
package com.wrangler.app;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;

/**
 * @author edenzik
 *
 */
class CSVUpload extends Upload {
	private final OutputStream csv;
	public CSVUpload() {
		csv = new ByteArrayOutputStream();
		setReceiver(new Receiver(){
			public OutputStream receiveUpload(String filename,String mimeType) {
				return csv;
			}
		});
	}
	
	OutputStream getOutputStream(){return csv;}

}
