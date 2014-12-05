/**
 * 
 */
package com.wrangler.fd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;

/**
 * @author edenzik
 *
 */
public class TableSelection extends ComboBox {

	/**
	 * 
	 */
	public TableSelection(Database db) {
		addContainerProperty("Table", Relation.class, null);
		setFilteringMode(FilteringMode.CONTAINS);
		db.getDbHelper().getRelations();
		Set<Relation> relations = null;
		this.addItems(relations);
	}
	
	


}
