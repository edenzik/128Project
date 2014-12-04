/**
 * 
 */
package com.wrangler.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.ui.Table;

/**
 * @author edenzik
 *
 */
public class QueryResult extends Table {

	/**
	 * @throws SQLException 
	 * 
	 */
	public QueryResult() {
		setSelectable(false);
		setImmediate(true);
		setSizeFull();

	}

}
