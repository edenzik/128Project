/**
 * 
 */
package com.wrangler.ui.fd;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.wrangler.load.Attribute;

/**
 * @author edenzik
 *
 */
class FDViolationWindow extends Window {
	Map<String, String> correctedValues = new HashMap<String,String>();
	String valueFrom = null;
	protected FDViolationWindow(Attribute fromAtt, Attribute toAtt, final Map<String, Map<String, Double>> valuePercent){
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
		fixViolations.setSizeFull();
		final VerticalSplitPanel tableButton = new VerticalSplitPanel(possibleValuesLayout, fixViolations);
		tableButton.setSplitPosition(80, Unit.PERCENTAGE);
		
		possibleValuesFrom.addItemClickListener(new ItemClickListener(){

			@Override
			public void itemClick(ItemClickEvent event) {
				valueFrom = event.getItem().getItemProperty("From Attribute").getValue().toString();
				possibleValuesTo.fill(valuePercent.get(event.getItem().getItemProperty("From Attribute").getValue()));
				
			}
			
		});
		fixViolations.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Object toAttId = possibleValuesTo.getValue();
				//Object valueFrom = possibleValuesFrom.getItem(possibleValuesFrom.getValue()).getItemProperty("From Attribute").getValue();
				//Object valueTo = possibleValuesTo.getItem(possibleValuesTo.getValue()).getItemProperty("Violating Values").getValue();
				if (toAttId!=null){
					System.out.println(valueFrom);
					System.out.println(possibleValuesTo.getItem(toAttId).getItemProperty("Violating Values").getValue());
					correctedValues.put(valueFrom, possibleValuesTo.getItem(toAttId).getItemProperty("Violating Values").getValue().toString());
					//System.out.println(possibleValuesTo.getItem(toAttId));
				}
				
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
