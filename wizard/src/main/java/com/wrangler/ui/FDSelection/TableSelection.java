/**
 * 
 */
package com.wrangler.ui.FDSelection;

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
	 * A table ComBO selector enabling you to choose 
	 * a table to display its attributes
	 * 
	 */
	public TableSelection(Database db) {
		setSizeFull();
		addContainerProperty("Table", Relation.class, null);
		setFilteringMode(FilteringMode.CONTAINS);
		Set<Relation> relations = db.getDbHelper().getRelations();
		this.addItems(relations);
	}
	
	


}
