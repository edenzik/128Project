/**
 * 
 */
package com.wrangler.query;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * @author edenzik
 *
 */
public class DatabaseBrowser extends HorizontalSplitPanel {

	/**
	 * 
	 */
	public DatabaseBrowser() {
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
		addComponent(new TablesList());
		addComponent(new QueryWindow());
		setSizeFull();
	}

}
