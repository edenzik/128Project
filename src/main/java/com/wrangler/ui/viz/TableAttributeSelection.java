/**
 * 
 */
package com.wrangler.ui.viz;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * A selection which chooses all the attributes of a table
 * @author edenzik
 *
 */
public class TableAttributeSelection extends VerticalSplitPanel{

	/**
	 * 
	 */
	public TableAttributeSelection(ComboBox cbox, Component attributeTable)  {
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
