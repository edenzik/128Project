/**
 * 
 */
package com.wrangler.ui.fd;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.wrangler.fd.SoftFD;
import com.wrangler.load.Attribute;
import com.wrangler.load.Database;

/**
 * @author edenzik
 *
 */
class FDViolationWindow extends Window {
	Map<String, String> correctedValues = new HashMap<String,String>();
	Item valueFrom = null;
	protected FDViolationWindow(final FDTable fdTable, final SoftFD fd, Attribute fromAtt, Attribute toAtt, final Map<String, Map<String, Double>> valuePercent, final Database db){
		final Table possibleValuesFrom = new Table();
		possibleValuesFrom.setSizeFull();
		possibleValuesFrom.setSelectable(true);
		final FDViolationTable possibleValuesTo = new FDViolationTable(toAtt);
		//possibleValuesTo.fill(valuePercent.keySet());
		possibleValuesTo.setSizeFull();
		HorizontalSplitPanel possibleValuesLayout = new HorizontalSplitPanel(possibleValuesFrom, possibleValuesTo);
		possibleValuesLayout.setSizeFull();
		possibleValuesLayout.setLocked(true);
		possibleValuesLayout.setSplitPosition(40, Unit.PERCENTAGE);

		possibleValuesFrom.addContainerProperty("From Attribute", String.class, null);
		
		//possibleValuesFrom.addItems(valuePercent.keySet());
		for (String att : valuePercent.keySet()){
			possibleValuesFrom.addItem(new String[]{att}, null);
		}
		Button fixViolations = new Button("Fix Violations");
		Button doneButton = new Button("Done");
		doneButton.setSizeFull();
		HorizontalSplitPanel fixDone = new HorizontalSplitPanel(fixViolations, doneButton);
		fixDone.setLocked(true);
		fixViolations.setSizeFull();
		final VerticalSplitPanel tableButton = new VerticalSplitPanel(possibleValuesLayout, fixDone);
		tableButton.setSplitPosition(80, Unit.PERCENTAGE);
		
		possibleValuesFrom.addItemClickListener(new ItemClickListener(){

			@Override
			public void itemClick(ItemClickEvent event) {
				valueFrom = event.getItem();
				possibleValuesTo.fill(valuePercent.get(event.getItem().getItemProperty("From Attribute").getValue()));
				
			}
			
		});
		fixViolations.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Object toAttId = possibleValuesTo.getValue();
				//Object valueFrom = possibleValuesFrom.getItem(possibleValuesFrom.getValue()).getItemProperty("From Attribute").getValue();
				//Object valueTo = possibleValuesTo.getItem(possibleValuesTo.getValue()).getItemProperty("Violating Values").getValue();
				if (toAttId!=null && valueFrom!=null){
					String correctedFromAttribute = valueFrom.getItemProperty("From Attribute").getValue().toString();
					correctedValues.put(correctedFromAttribute, possibleValuesTo.getItem(toAttId).getItemProperty("Violating Values").getValue().toString());
					possibleValuesFrom.removeAllItems();
					possibleValuesTo.removeAllItems();
					valuePercent.remove(correctedFromAttribute);
					for (String att : valuePercent.keySet()){
						possibleValuesFrom.addItem(new String[]{att}, null);
					}
				}
				
			}
		});
		
		doneButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				db.getDbHelper().fixAllViolations(fd, correctedValues);
				Notification.show("Sucesss",
		                  "Violations have been fixed for the attribute.",
		                  Notification.Type.HUMANIZED_MESSAGE);
				fdTable.insert(fd);
				close();
				
			}
		});
		
		setCaption("CONFLICT: " + fromAtt + " has multiple values of " + toAtt);
		setContent(tableButton);
		initLayout();

	}

	private void initLayout(){
		setHeight("40%");
		setWidth("40%");
		setDraggable(false);
		setResizable(false);
		center();
	}

}
