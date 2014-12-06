/**
 * 
 */
package com.wrangler.ui.fd;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
import com.wrangler.ui.login.User;

/**
 * A selection which chooses all the attributes of a table
 * @author edenzik
 *
 */
public class TableAttributeSelection extends VerticalSplitPanel{
	private final ComboBox cbox;

	/**
	 * 
	 */
	public TableAttributeSelection(final User user) {
		initLayout();
		cbox = new TableSelection(user.getDB());

		final AttributeTable atable = new AttributeTable();
		HorizontalLayout hl = new HorizontalLayout(cbox);
		addComponent(hl);

		hl.setSizeFull();
		hl.setMargin(new MarginInfo(true, false, false, false));
		cbox.addValueChangeListener(new ComboBox.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue()!=null){
					atable.fill(user.getDB().getDbHelper().getRelationAttributes(RelationFactory.createRelation(event.getProperty().getValue().toString(), user.getDB())));
				}

			}
		});
		addComponent(atable);

	}


	private void initLayout(){
		setSplitPosition(6, Unit.PERCENTAGE);
		setLocked(true);
	}
	
	ComboBox getCbox(){return cbox;}


}
