/**
 * 
 */
package com.wrangler.ui.fd;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.wrangler.ui.login.User;

/**
 * The window containing all of the functional dependency
 * inference stuff.
 * @author edenzik
 *
 */
public class FDWindow extends Window {

	/**
	 * The window container, sets up the FD panel
	 * 
	 */
	public FDWindow(UI ui, User user) {
		super("Functional Dependency Detection");
		initLayout();
		setContent(new FDPanel(user));
	}
	
	private void initLayout(){
		setHeight("90%");
		setWidth("90%");
		setDraggable(false);
		setResizable(false);
		center();
	}

}
