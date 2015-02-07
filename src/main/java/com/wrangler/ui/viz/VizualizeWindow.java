/**
 * 
 */
package com.wrangler.ui.viz;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.wrangler.ui.callback.Callback;
import com.wrangler.ui.login.User;

/**
 * The window containing all of the functional dependency
 * inference stuff.
 * @author edenzik
 *
 */
public class VizualizeWindow extends Window {

	/**
	 * The window container, sets up the FD panel
	 * 
	 */
	public VizualizeWindow(UI ui, User user, Callback callback) {
		super("Vizualization Window");
		initLayout();
		setContent((new BasicColumn()).getChart());
	}
	
	private void initLayout(){
		setHeight("90%");
		setWidth("90%");
		setDraggable(false);
		setResizable(false);
		center();
	}

}
