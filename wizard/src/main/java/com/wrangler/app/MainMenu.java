/**
 * 
 */
package com.wrangler.app;

import com.vaadin.ui.MenuBar;

/**
 * @author edenzik
 *
 */
public class MainMenu extends MenuBar {

	/**
	 * 
	 */
	public MainMenu(final Wizard ui) {
		MenuBar.Command command = new MenuBar.Command() {
		    public void menuSelected(MenuItem selectedItem) {
		        ui.initUpload();
		    }  
		};
		addItem("Start New Project", null, null);
		addItem("Import Spreadsheet Data", null, command);
		addItem("Wrangle Spreadsheet Data", null, null);
		addItem("Infer Functional Dependencies", null, null);
		addItem("Export Relational Data", null, null);
		addItem("Save Project and Logout", null, null);
		setSizeFull();
	}

}
