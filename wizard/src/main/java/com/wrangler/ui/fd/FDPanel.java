/**
 * 
 */
package com.wrangler.ui.fd;

import java.sql.SQLException;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.UI;
import com.wrangler.fd.FDDetector;
import com.wrangler.load.RelationFactory;
import com.wrangler.load.TableNotFoundException;
import com.wrangler.ui.login.User;

/**
 * @author edenzik
 *
 */
class FDPanel extends HorizontalSplitPanel {

	/**
	 * A panel containng the whole content of the window
	 * 
	 */
	FDPanel(final User user) {
		initLayout();
		
		ComboBox tableSelection = new TableSelection(user.getDB());
		final AttributeTable attributeTable = new AttributeTable();
		
		TableAttributeSelection tableAttributeSelection = new TableAttributeSelection(tableSelection, attributeTable);
		final FDTable fdTable = new FDTable();
		FDSelectionLayout fdSelectionLayout = new FDSelectionLayout(fdTable);
		


		tableSelection.addValueChangeListener(new ComboBox.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue()!=null){
					attributeTable.fill(user.getDB().getDbHelper().getRelationAttributes(RelationFactory.createRelation(event.getProperty().getValue().toString(), user.getDB())));
					fdTable.fill(RelationFactory.createRelation(event.getProperty().getValue().toString(), user.getDB()).findAllHardFds());
				}

			}
		});
		addComponent(tableAttributeSelection);
		addComponent(fdSelectionLayout);
		
	}

	private void initLayout(){
		setSizeFull();
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
	}

}
