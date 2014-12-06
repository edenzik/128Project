package com.wrangler.ui.fd;

import java.util.HashMap;
import java.util.Set;

import com.vaadin.ui.Table;
import com.wrangler.fd.FunctionalDependency;

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
		setSelectable(true);
	}
	
	void fill(Set<FunctionalDependency> functionalDependencies){
		removeAllItems();
		HashMap<Integer, FunctionalDependency> fdSet = new HashMap<Integer, FunctionalDependency>();
		for (FunctionalDependency fd: functionalDependencies){
			fdSet.put((Integer) addItem(new String[]{fd.getFromAtt().getName(), fd.getToAtt().getName()}, null), fd);
		}
	}

}
