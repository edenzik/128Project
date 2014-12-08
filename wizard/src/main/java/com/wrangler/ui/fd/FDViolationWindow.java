/**
 * 
 */
package com.wrangler.ui.fd;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.wrangler.fd.FunctionalDependency;
import com.wrangler.fd.SoftFD;

/**
 * @author edenzik
 *
 */
class FDViolationWindow extends Window {
	protected FDViolationWindow(SoftFD fd){
		super("CONFLICT: " + fd.getFromAtt() + " has multiple values of " + fd.getToAtt());
		initLayout();
		FDViolationTable fdViolationTable = new FDViolationTable(fd.getToAtt());
		fdViolationTable.fill(fd.getViolations());
		//VerticalLayout layout = new VerticalLayout(fdViolationTable);
		//layout.setMargin(new MarginInfo(true, false, false, false));
		setContent(fdViolationTable);
	}
	
	private void initLayout(){
		setHeight("40%");
		setWidth("40%");
		setDraggable(false);
		setResizable(false);
		center();
	}

}
