package com.wrangler.ui.query;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextArea;

/**
 * Query exeuction field class with the button to run a query
 * @author edenzik
 *
 */
public class QueryExecutionField extends HorizontalSplitPanel {
	private static final long serialVersionUID = 1L;
	private final TextArea sqlField;
	private final Button runQuery;

	/**
	 * This is the field letting you execute the query, displays the result
	 * in QueryResult.
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
	
	
	/**
	 * Initializes the basic layout
	 * 
	 */
	private void initLayout(){
		setSplitPosition(91, Unit.PERCENTAGE);
		setLocked(true);
		setSizeFull();
	}
	
	String getQuery(){return sqlField.getValue();}
	Button getButton(){return runQuery;}

}
