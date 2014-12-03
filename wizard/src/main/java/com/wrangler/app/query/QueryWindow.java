/**
 * 
 */
package com.wrangler.app.query;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.app.query.QueryExecutionField;

/**
 * Main window containing the table panel and the running panel
 * @author edenzik
 *
 */
public class QueryWindow extends VerticalSplitPanel {

	/**
	 * 
	 */
	public QueryWindow() {
		setSplitPosition(80, Unit.PERCENTAGE);
		setLocked(true);
		addComponent(new QueryResult());
		addComponent(new QueryExecutionField());
		
	}

}
