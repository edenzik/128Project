/**
 * 
 */
package com.wrangler.fd;

import java.sql.ResultSet;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;
import com.wrangler.login.User;

/**
 * A selection which chooses all the attributes of a table
 * @author edenzik
 *
 */
public class TableAttributeSelection extends VerticalSplitPanel {

	/**
	 * 
	 */
	public TableAttributeSelection(User user) {
		initLayout();
		TableSelection cbox = new TableSelection(user.getDB());
		final AttributeTable atable = new AttributeTable();
		addComponent(cbox);
		cbox.addValueChangeListener(new ComboBox.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				//atable.fill(user.getDB().getDbHelper().getTableAttributes(new event.getProperty().getValue()));
				
			}
		});
		addComponent(atable);
		
	}
	
	
	private void initLayout(){
		setSplitPosition(10, Unit.PERCENTAGE);
		setLocked(true);
	}


}
