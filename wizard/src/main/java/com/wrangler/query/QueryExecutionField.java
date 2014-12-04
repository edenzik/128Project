/**
 * 
 */
package com.wrangler.query;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextArea;

/**
 * @author edenzik
 *
 */
public class QueryExecutionField extends HorizontalSplitPanel {
	private final TextArea sqlField;
	private final Button runQuery;

	/**
	 * 
	 */
	public QueryExecutionField() {
		setSplitPosition(90, Unit.PERCENTAGE);
		runQuery = new Button("Run");
		runQuery.setSizeFull();
		sqlField = new TextArea();
		sqlField.setSizeFull();
		addComponent(sqlField);
		addComponent(runQuery);
		setLocked(true);
		setSizeFull();
	}
	
	public TextArea getField(){return sqlField;}
	public Button getButton(){return runQuery;}

}
