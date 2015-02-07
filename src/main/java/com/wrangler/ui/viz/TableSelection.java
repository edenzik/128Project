/**
 * 
 */
package com.wrangler.ui.viz;

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
	public TableSelection(Set<Relation> relations) {
		setSizeFull();
		addContainerProperty("Table", Relation.class, null);
		setFilteringMode(FilteringMode.CONTAINS);
		this.addItems(relations);
	}
	
	


}
