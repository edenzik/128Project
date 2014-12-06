/**
 * 
 */
package com.wrangler.fd;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
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
	public TableAttributeSelection(final User user) {
		
		initLayout();
		TableSelection cbox = new TableSelection(user.getDB());
		final AttributeTable atable = new AttributeTable();
		addComponent(cbox);
		cbox.addValueChangeListener(new ComboBox.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					atable.fill(user.getDB().getDbHelper().getRelationAttributes(RelationFactory.createRelation(event.getProperty().getValue().toString(), user.getDB())));

				
			}
		});
		addComponent(atable);
		
	}
	
	
	private void initLayout(){
		setSplitPosition(8, Unit.PERCENTAGE);
		setLocked(true);
	}


}
