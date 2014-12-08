package com.wrangler.ui.fd;

import java.util.HashMap;
import java.util.Set;

import org.seleniumhq.jetty7.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Table;
import com.wrangler.fd.FunctionalDependency;
import com.wrangler.load.Relation;

/**
 * @author edenzik
 *
 */
public class FDTable extends Table {
	private Relation rel = null;
	private HashMap<Object, FunctionalDependency> fdSet;
	
	private static final Logger LOG = LoggerFactory.getLogger(FDTable.class);

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
	
	public void fill(Relation rel){
		this.rel = rel;
		Set<FunctionalDependency> functionalDependencies = rel.findAllHardFds();
		removeAllItems();
		fdSet = new HashMap<Object, FunctionalDependency>();
		for (FunctionalDependency fd: functionalDependencies){
			insert(fd);
		}
	}
	
	public void removeSelectedValue(){
		Object currentValue = getValue();
		if (currentValue!=null) {
			LOG.debug(getValue().toString());
			rel.removeFd(fdSet.remove(currentValue));
			removeItem(currentValue);
		}
	}
	
	public void insert(FunctionalDependency fd){
		fdSet.put(addItem(new String[]{fd.getFromAtt().getName(), fd.getToAtt().getName()}, null), fd);
	}
	
	protected Relation getRel(){return rel;}
	
	protected HashMap<Object, FunctionalDependency> getFdSet(){return fdSet;}

}
