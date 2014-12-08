/**
 * 
 */
package com.wrangler.ui.normalize;

import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
import com.wrangler.normalization.Normalizer;
import com.wrangler.ui.login.User;
import com.wrangler.ui.query.TablesList;
import com.wrangler.ui.callback.Callback;
import com.wrangler.ui.fd.FDSelectionLayout;
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
	NormalizePanel(final User user, final Callback callback) {
		initLayout();
		
		Set<Relation > relations = user.getDB().getDbHelper().getRelations();
		
		TableSelection tableSelection = new TableSelection(relations);
		final FDTable fdTable = new FDTable();
		fdTable.setSelectable(false);
		TableAttributeSelection tableAttributeSelection = new TableAttributeSelection(tableSelection, fdTable);
		
		
		

		final NormalizedTablesList normalizedTablesList = new NormalizedTablesList(user.getDB());
		final Button createTable = new Button("Create Tables");
		createTable.setSizeFull();
		tableSelection.addValueChangeListener(new ComboBox.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue()!=null){
					final Relation selectedRelation = RelationFactory.createExistingRelation(event.getProperty().getValue().toString(), user.getDB());
					fdTable.fill(selectedRelation.findAllHardFds());
					Normalizer norm = Normalizer.newInstance(RelationFactory.createExistingRelation(event.getProperty().getValue().toString(), user.getDB()));
					
					final Set<Relation> normalizedRelations = norm.bcnf();
					normalizedTablesList.load(normalizedRelations);
					createTable.addClickListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							selectedRelation.decomposeInto(normalizedRelations);
							callback.execute();
							Notification.show("Done!",
					                  "Tables have been updated",
					                  Notification.Type.HUMANIZED_MESSAGE);
						}
					});
				}

			}
		});
		addComponent(tableAttributeSelection);
		
		
		
		
		VerticalSplitPanel tablesListButton = new VerticalSplitPanel(normalizedTablesList, createTable);
		tablesListButton.setSizeFull();
		tablesListButton.setSplitPosition(95, Unit.PERCENTAGE);
		tablesListButton.setLocked(true);
		addComponent(tablesListButton);
		
	}

	private void initLayout(){
		setSizeFull();
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
	}

}
