/**
 * 
 */
package com.wrangler.ui.query;

import java.sql.SQLException;
import java.util.Set;

import com.vaadin.ui.Table;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;

/**
 * @author edenzik
 *
 */
public class TablesList extends Table {
	private final Database db;

	/**
	 * Gets the list of tables in this database schema
	 * Dynamically reloads.
	 * @throws SQLException 
	 * @throws UnsupportedOperationException 
	 * 
	 */
	public TablesList(Database db) {
		this.db = db;
		initLayout();
		reload();
	}
	
	void reload(){
		removeAllItems();
		load(db.getDbHelper().getRelations());
	}
	
	public void load(Set<Relation> relations){
		try {
			for(Relation rel : relations) {
				addItem(new String[]{rel.getName()}, null);
			}
		} catch (UnsupportedOperationException e) {
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
