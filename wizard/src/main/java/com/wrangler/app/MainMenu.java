/**
 * 
 */
package com.wrangler.app;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.wrangler.login.User;
import com.wrangler.upload.UploadWindow;

/**
 * @author edenzik
 *
 */
public class MainMenu extends MenuBar {

	/**
	 * Menu is attached to the main UI by the login window
	 * Once a button is pressed, it is attached to the UI
	 * 
	 */
	public MainMenu(final UI ui, final User user) {
		MenuBar.Command command = new MenuBar.Command() {
		    public void menuSelected(MenuItem selectedItem) {
		        ui.addWindow(new UploadWindow(ui, user));
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
