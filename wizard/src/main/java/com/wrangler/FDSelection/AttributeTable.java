/**
 * 
 */
package com.wrangler.FDSelection;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;
import com.wrangler.load.Attribute;

/**
 * @author edenzik
 *
 */
public class AttributeTable extends Table {

	/**
	 * 
	 */
	public AttributeTable() {
		initLayout();
		this.
		addContainerProperty("Attributes", String.class, null);
	}
	
	private void initLayout(){
		setSizeFull();
	}
	
	void fill(Set<Attribute> attributes){
		removeAllItems();
		for (Attribute att: attributes){
			addItem(new String[]{att.getName()}, null);
		}
	}

}
