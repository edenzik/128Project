/**
 * 
 */
package com.wrangler.app.upload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.ui.Upload;

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
	}
	
	public OutputStream getOutputStream(){return csv;}

}
