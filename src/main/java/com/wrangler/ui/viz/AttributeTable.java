/**
 * 
 */
package com.wrangler.ui.viz;

import java.util.Set;

import com.vaadin.ui.Table;
import com.wrangler.load.Attribute;

/**
 * @author edenzik
 *
 */
class AttributeTable extends Table {

	/**
	 * 
	 */
	AttributeTable() {
		initLayout();
		addContainerProperty("Attributes", String.class, null);
	}
	
	private void initLayout(){
		setSizeFull();
	}
	
	void fill(Set<Attribute> attributes){
		removeAllItems();
		for (Attribute att: attributes){
			addItem(new String[]{att.toString()}, null);
		}
	}
	
	

}
