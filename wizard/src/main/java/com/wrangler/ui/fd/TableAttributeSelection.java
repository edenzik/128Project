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
import com.vaadin.ui.Component;
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
class TableAttributeSelection extends VerticalSplitPanel{

	/**
	 * 
	 */
	TableAttributeSelection(ComboBox cbox, Component attributeTable)  {
		initLayout();
		HorizontalLayout hl = new HorizontalLayout(cbox);
		cbox.setInvalidAllowed(false);
		cbox.setInputPrompt("Select table here...");
		addComponent(hl);
		hl.setSizeFull();
		hl.setMargin(new MarginInfo(true, false, false, false));
		

		addComponent(attributeTable);

	}


	private void initLayout(){
		setSplitPosition(6, Unit.PERCENTAGE);
		setLocked(true);
	}

}
