/**
 * 
 */
package com.wrangler.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;
import com.wrangler.load.Database;

/**
 * @author edenzik
 *
 */
public class TablesList extends Table {
	private final Database db;

	/**
	 * Gets the list of tables in this database schema
	 * @throws SQLException 
	 * @throws UnsupportedOperationException 
	 * 
	 */
	public TablesList(Database db) {
		this.db = db;
		initLayout();
		load();
	}
	
	void reload(){
		removeAllItems();
		load();
	}
	
	void load(){
		try {
			ResultSet tables = db.getDbHelper().getTables();
			while (tables.next()) {
				addItem(new String[]{tables.getString("TABLE_NAME")}, null);
			}
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private void initLayout(){
		addContainerProperty("Table", String.class, null);
		setSelectable(true);
		setImmediate(true);
		setSizeFull();
	}
}
