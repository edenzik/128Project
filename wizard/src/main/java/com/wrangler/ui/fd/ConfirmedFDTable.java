package com.wrangler.ui.fd;

import java.util.Set;

import com.vaadin.ui.Table;
import com.wrangler.fd.FunctionalDependency;

/**
 * @author edenzik
 *
 */
public class ConfirmedFDTable extends Table {

	/**
	 * 
	 */
	public ConfirmedFDTable() {
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
