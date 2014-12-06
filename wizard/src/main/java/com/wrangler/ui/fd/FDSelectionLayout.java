package com.wrangler.ui.fd;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;

/**
 * 
 */

/**
 * @author edenzik
 *
 */
public class FDSelectionLayout extends HorizontalSplitPanel {

	/**
	 * 
	 */
	public FDSelectionLayout() {
		addComponent(new FDTable());
	}

}
