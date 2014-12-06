/**
 * 
 */
package com.wrangler.ui.fd;

import java.sql.SQLException;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.UI;
import com.wrangler.fd.FDDetector;
import com.wrangler.load.Relation;
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
		
		Set<Relation > relations = user.getDB().getDbHelper().getRelations();
		
		TableSelection tableSelection = new TableSelection(relations);
		final AttributeTable attributeTable = new AttributeTable();
		
		TableAttributeSelection tableAttributeSelection = new TableAttributeSelection(tableSelection, attributeTable);
		final FDTable fdTable = new FDTable();
		FDSelectionLayout fdSelectionLayout = new FDSelectionLayout(fdTable, tableSelection, user.getDB());
		


		tableSelection.addValueChangeListener(new ComboBox.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue()!=null){
					attributeTable.fill(user.getDB().getDbHelper().getRelationAttributes(RelationFactory.createRelation(event.getProperty().getValue().toString(), user.getDB())));
					FDDetector detector = new FDDetector(user.getDB());
					Relation relation = RelationFactory.createRelation(event.getProperty().getValue().toString(), user.getDB());
					fdTable.fill(detector.findAllHardFds(relation));
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
