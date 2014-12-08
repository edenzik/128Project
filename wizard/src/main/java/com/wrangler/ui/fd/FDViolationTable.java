package com.wrangler.ui.fd;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Table;
import com.wrangler.fd.FunctionalDependency;
import com.wrangler.load.Attribute;

class FDViolationTable extends Table {
	//private HashMap<Object, Attribute> fdSet;
	protected FDViolationTable(Attribute fdTo){
		initLayout();
		addContainerProperty("Violating Value of " + fdTo.getName(), String.class, null);
		addContainerProperty("Percent", String.class, null);
	}
	
	protected void fill(Map<String, Double> valuePercent){
		for (String fdToValue: valuePercent.keySet()){
			insert(fdToValue, valuePercent.get(fdToValue));
		}
	}
	
	private void insert(String value, Double percent){
		addItem(new String[]{value, percent + "%"}, null);
	}
	
	private void initLayout(){
		setSizeFull();
		setSelectable(true);
	}
}
