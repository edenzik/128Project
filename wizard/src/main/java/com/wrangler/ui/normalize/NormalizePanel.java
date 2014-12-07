/**
 * 
 */
package com.wrangler.ui.normalize;

import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
import com.wrangler.ui.login.User;
import com.wrangler.ui.fd.FDTable;
import com.wrangler.ui.fd.TableAttributeSelection;
import com.wrangler.ui.fd.TableSelection;

/**
 * @author edenzik
 *
 */
class NormalizePanel extends HorizontalSplitPanel {

	/**
	 * A panel containng the whole content of the window
	 * 
	 */
	NormalizePanel(final User user) {
		initLayout();
		
		Set<Relation > relations = user.getDB().getDbHelper().getRelations();
		
		TableSelection tableSelection = new TableSelection(relations);
		final FDTable fdTable = new FDTable();
		fdTable.setSelectable(false);
		TableAttributeSelection tableAttributeSelection = new TableAttributeSelection(tableSelection, fdTable);
		
		FDSelectionLayout fdSelectionLayout = new FDSelectionLayout(new FDTable(), tableSelection, user.getDB());
		


		tableSelection.addValueChangeListener(new ComboBox.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue()!=null){
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
