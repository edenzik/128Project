/**
 * 
 */
package com.wrangler.app;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.wrangler.fd.FDWindow;
import com.wrangler.login.User;
import com.wrangler.upload.UploadWindow;

/**
 * @author edenzik
 *
 */
public class MainMenu extends MenuBar {
	private final UI ui;
	private final User user;

	/**
	 * Menu is attached to the main UI by the login window
	 * Once a button is pressed, it is attached to the UI
	 * 
	 */
	public MainMenu(UI ui, User user) {
		this.ui = ui;
		this.user = user;

		addItem("Start New Project", newProject());
		addItem("Import Spreadsheet Data", importData());
		addItem("Wrangle Spreadsheet Data", wrangleData());
		addItem("Infer Functional Dependencies", inferFD());
		addItem("Export Relational Data", exportData());
		addItem("Save Project and Logout", saveLogout());
		setSizeFull();
	}
	
	private Command newProject(){return null;}
	private Command importData(){
		return new MenuBar.Command() {
		    public void menuSelected(MenuItem selectedItem) {
		        ui.addWindow(new UploadWindow(ui, user));
		    }
		};
	}
	private Command wrangleData(){return null;}
	private Command inferFD(){
		return new MenuBar.Command() {
		    public void menuSelected(MenuItem selectedItem) {
		        ui.addWindow(new FDWindow(ui, user));
		    }
		};
	}
	private Command exportData(){return null;}
	private Command saveLogout(){return null;}
	
}
