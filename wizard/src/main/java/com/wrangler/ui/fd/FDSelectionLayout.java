package com.wrangler.ui.fd;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * 
 */

/**
 * @author edenzik
 *
 */
class FDSelectionLayout extends VerticalSplitPanel {

	/**
	 * Enables the user to see all functional dependencies and add/remove some
	 * 
	 */
	FDSelectionLayout(FDTable fdTable) {
		initLayout();
		HorizontalLayout hl = new HorizontalLayout(fdTable);
		addComponent(hl);
		hl.setSizeFull();
		hl.setMargin(new MarginInfo(true, false, false, false));
		addComponent(new FDAddRemovePanel(fdTable));
	}
	
	private void initLayout(){
		setSizeFull();
		setSplitPosition(80, Unit.PERCENTAGE);
		setLocked(true);
	}
	
	

}
