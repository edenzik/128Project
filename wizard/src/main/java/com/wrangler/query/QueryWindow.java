package com.wrangler.query;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Button.ClickEvent;
import com.wrangler.load.Database;
import com.wrangler.query.QueryExecutionField;

/**
 * Main window containing the table panel and the running panel
 * @author edenzik
 *
 */
public class QueryWindow extends VerticalSplitPanel {
	private final Database db;
	private final QueryResult result;

	/**
	 * A window letteing the user input a query
	 * 
	 */
	public QueryWindow(Database db){
		initLayout();
		this.db = db;
		result = new QueryResult();
		
		final QueryExecutionField field = new QueryExecutionField();
		field.getButton().addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				displayQuery(field.getQuery());
				
			}
		});
		addComponent(result);
		addComponent(field);
		
	}
	
	private void initLayout(){
		setSplitPosition(90, Unit.PERCENTAGE);
		setLocked(true);
	}

	void displayQuery(String sql){
		try {
			result.setContainerDataSource(new SQLContainer(new FreeformQuery(sql,db.getDbHelper().getPool())));
		} catch (SQLException e) {
			Notification.show("SQL Error!",
					e.getMessage(),
					Notification.Type.ERROR_MESSAGE);
		}
	}
}
