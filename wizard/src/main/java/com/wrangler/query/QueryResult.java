package com.wrangler.query;

import java.sql.SQLException;
import com.vaadin.ui.Table;

/**
 * @author edenzik
 *
 */
public class QueryResult extends Table {

	/**
	 * @throws SQLException 
	 * A window showing the query result table.
	 */
	public QueryResult() {
		setSelectable(false);
		setImmediate(true);
		setSizeFull();

	}

}
