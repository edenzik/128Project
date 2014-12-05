/**
 * 
 */
package com.wrangler.fd;

import java.util.Set;

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
		this.setHeight(90, Unit.PERCENTAGE);
		addContainerProperty("Table", Relation.class, null);
		setFilteringMode(FilteringMode.CONTAINS);
		Set<Relation> relations = db.getDbHelper().getTables();
		this.addItems(relations);
	}
	
	


}
