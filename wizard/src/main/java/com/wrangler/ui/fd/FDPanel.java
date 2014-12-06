/**
 * 
 */
package com.wrangler.ui.fd;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.UI;
import com.wrangler.ui.login.User;

/**
 * @author edenzik
 *
 */
public class FDPanel extends HorizontalSplitPanel {

	/**
	 * A panel containng the whole content of the window
	 * 
	 */
	public FDPanel(User user) {
		initLayout();
		addComponent(new TableAttributeSelection(user));
	}
	
	private void initLayout(){
		setSizeFull();
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
	}

}
