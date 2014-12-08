package com.wrangler.ui.fd;

import java.util.HashMap;
import java.util.Set;

import com.vaadin.ui.Table;
import com.wrangler.fd.FunctionalDependency;
import com.wrangler.load.Relation;

/**
 * @author edenzik
 *
 */
public class FDTable extends Table {
	private HashMap<Object, FunctionalDependency> fdSet;

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
	
	public void fill(Set<FunctionalDependency> functionalDependencies){
		removeAllItems();
		fdSet = new HashMap<Object, FunctionalDependency>();
		for (FunctionalDependency fd: functionalDependencies){
			insert(fd);
		}
	}
	
	public void removeSelectedValue(){
		Object currentValue = getValue();
		removeItem(currentValue);
		if (currentValue!=null) fdSet.remove(currentValue);
	}
	
	public void insert(FunctionalDependency fd){
		fdSet.put(addItem(new String[]{fd.getFromAtt().getName(), fd.getToAtt().getName()}, null), fd);
	}
	
	protected HashMap<Object, FunctionalDependency> getFdSet(){return fdSet;}

}
