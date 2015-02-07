/**
 * 
 */
package com.wrangler.ui.viz;

import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
import com.wrangler.ui.login.User;

/**
 * @author edenzik
 *
 */
class VizPanel extends HorizontalSplitPanel {

	/**
	 * A panel containng the whole content of the window
	 * 
	 */
	VizPanel(UI ui, final User user) {
		initLayout();
		
		Set<Relation> relations = user.getDB().getDbHelper().getRelations();
		
		TableSelection tableSelection = new TableSelection(relations);
		final AttributeTable attributeTable = new AttributeTable();
		
		TableAttributeSelection tableAttributeSelection = new TableAttributeSelection(tableSelection, attributeTable);
		final FDTable fdTable = new FDTable();



		tableSelection.addValueChangeListener(new ComboBox.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue()!=null){
					attributeTable.fill(user.getDB().getDbHelper().getRelationAttributes(RelationFactory.createExistingRelation(event.getProperty().getValue().toString(), user.getDB())));

				}

			}
		});
		addComponent(tableAttributeSelection);
		addComponent((new BasicColumn()).getChart());

		
	}

	private void initLayout(){
		setSizeFull();
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
	}

}
