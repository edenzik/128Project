package com.wrangler.ui.normalize;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;
import com.wrangler.ui.fd.FDTable;
import com.wrangler.ui.fd.TableSelection;

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
	FDSelectionLayout(FDTable fdTable, TableSelection tableSelection, Database db) {
		initLayout();
		HorizontalLayout hl = new HorizontalLayout(fdTable);
		addComponent(hl);
		hl.setSizeFull();
		hl.setMargin(new MarginInfo(true, false, false, false));
	}
	
	private void initLayout(){
		setSizeFull();
		setSplitPosition(80, Unit.PERCENTAGE);
		setLocked(true);
	}
	
	

}
