/**
 * 
 */
package com.wrangler.ui.export;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.wrangler.ui.login.User;

/**
 * @author edenzik
 *
 */
public class ExportWindow extends Window {

	/**
	 * 
	 */
	public ExportWindow(UI ui, User user) {
		super("Export Data");
		initLayout();
		setContent(new ExportPanel(user));
	}
	
	private void initLayout(){
		setHeight("60%");
		setWidth("60%");
		setDraggable(false);
		setResizable(false);
		center();
	}

}
