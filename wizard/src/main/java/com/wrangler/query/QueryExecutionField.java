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
		initLayout();
		runQuery = new Button("Run");
		runQuery.setSizeFull();
		sqlField = new TextArea();
		sqlField.setSizeFull();
		addComponent(sqlField);
		addComponent(runQuery);

	}
	
	private void initLayout(){
		setSplitPosition(93, Unit.PERCENTAGE);
		setLocked(true);
		setSizeFull();
	}
	
	String getQuery(){return sqlField.getValue();}
	Button getButton(){return runQuery;}

}
