/**
 * 
 */
package com.wrangler.query;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.load.Database;
import com.wrangler.query.QueryExecutionField;

/**
 * Main window containing the table panel and the running panel
 * @author edenzik
 *
 */
public class QueryWindow extends VerticalSplitPanel {

	/**
	 * @throws SQLException 
	 * 
	 */
	public QueryWindow(Database db) throws SQLException {
		setSplitPosition(80, Unit.PERCENTAGE);
		setLocked(true);
		QueryResult result = new QueryResult();
		result.setContainerDataSource(new SQLContainer(new FreeformQuery("SELECT * FROM table156",db.getDbHelper().getPool())));
		addComponent(result);
		addComponent(new QueryExecutionField());
	}

}
