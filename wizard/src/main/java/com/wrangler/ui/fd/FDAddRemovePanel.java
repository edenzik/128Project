/**
 * 
 */
package com.wrangler.ui.fd;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * @author edenzik
 *
 */
class FDAddRemovePanel extends HorizontalSplitPanel {

	/**
	 * 
	 */
	FDAddRemovePanel(final FDTable table) {
		initLayout();
		Button removeFD = new Button("Remove FD");
		removeFD.setSizeFull();
		removeFD.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				table.removeSelectedValue();
			}
		});
		
		addComponent(removeFD);
		
		Button addFD = new Button("Add FD");
		
		
		
		ListSelect selectFrom = new ListSelect("Left Side");
		ListSelect selectTo = new ListSelect("Right Side");
		HorizontalLayout FDSelectionLayout = new HorizontalLayout(selectFrom, selectTo);
		
		VerticalSplitPanel addFDSplitPanel = new VerticalSplitPanel(FDSelectionLayout, addFD);
		
		addComponent(addFDSplitPanel);
		
	}
	
	private void initLayout(){
		setSizeFull();
		setSplitPosition(50, Unit.PERCENTAGE);
		setLocked(true);
	}

}
