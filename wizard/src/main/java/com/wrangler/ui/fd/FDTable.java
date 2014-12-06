package com.wrangler.ui.fd;

import java.util.HashMap;
import java.util.Set;

import com.vaadin.ui.Table;
import com.wrangler.fd.FunctionalDependency;

/**
 * @author edenzik
 *
 */
class FDTable extends Table {
	HashMap<Object, FunctionalDependency> fdSet;

	/**
	 * 
	 */
	FDTable() {
		initLayout();
		addContainerProperty("From Attribute", String.class, null);
		addContainerProperty("To Attribute", String.class, null);
	}
	
	private void initLayout(){
		setSizeFull();
		setSelectable(true);
	}
	
	void fill(Set<FunctionalDependency> functionalDependencies){
		removeAllItems();
		fdSet = new HashMap<Object, FunctionalDependency>();
		for (FunctionalDependency fd: functionalDependencies){
			fdSet.put(addItem(new String[]{fd.getFromAtt().getName(), fd.getToAtt().getName()}, null), fd);
		}
	}
	
	void removeSelectedValue(){
		Object currentValue = getValue();
		removeItem(currentValue);
		if (currentValue!=null) fdSet.remove(currentValue);
	}

}
