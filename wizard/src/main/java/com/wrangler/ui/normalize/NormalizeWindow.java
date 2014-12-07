/**
 * 
 */
package com.wrangler.ui.normalize;

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
public class NormalizeWindow extends Window {

	/**
	 * The window container, sets up the FD panel
	 * 
	 */
	public NormalizeWindow(UI ui, User user, Callback callback) {
		super("Normalization Window");
		initLayout();
		setContent(new NormalizePanel(user, callback));
	}
	
	private void initLayout(){
		setHeight("90%");
		setWidth("90%");
		setDraggable(false);
		setResizable(false);
		center();
	}

}
