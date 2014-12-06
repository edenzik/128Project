/**
 * 
 */
package com.wrangler.ui.fd;

import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;
import com.wrangler.fd.FunctionalDependency;
import com.wrangler.load.Attribute;

/**
 * @author edenzik
 *
 */
public class FDTable extends Table {

	/**
	 * 
	 */
	public FDTable() {
		initLayout();
		addContainerProperty("From Attribute", String.class, null);
		addContainerProperty("To Attribute", String.class, null);
	}
	
	private void initLayout(){
		setSizeFull();
	}
	
	void fill(Set<FunctionalDependency> functionalDependencies){
		removeAllItems();
		for (FunctionalDependency fd: functionalDependencies){
			addItem(new String[]{fd.getFromAtt().getName(), fd.getToAtt().getName()}, null);
		}
	}

}
