/**
 * 
 */
package com.wrangler.ui.fd;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Button.ClickEvent;

/**
 * @author edenzik
 *
 */
class FDAddRemovePanel extends HorizontalSplitPanel {

	/**
	 * 
	 */
	FDAddRemovePanel(final FDTable table) {
		Button removeFD = new Button("Remove FD");
		removeFD.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				table.removeSelectedValue();
			}
		});
		addComponent(removeFD);
	}
	
	private void initLayout(){
		setSizeFull();
		setSplitPosition(80, Unit.PERCENTAGE);
		setLocked(true);
	}

}
