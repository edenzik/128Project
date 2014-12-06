/**
 * 
 */
package com.wrangler.ui.main;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.wrangler.ui.callback.Callback;
import com.wrangler.ui.fd.FDWindow;
import com.wrangler.ui.login.User;
import com.wrangler.ui.query.DatabaseBrowser;
import com.wrangler.ui.upload.UploadWindow;

/**
 * @author edenzik
 *
 */
public class MainMenu extends MenuBar {
	private final UI ui;
	private final User user;
	private final Callback callback;

	/**
	 * Menu is attached to the main UI by the login window
	 * Once a button is pressed, it is attached to the UI
	 * 
	 */
	public MainMenu(UI ui, User user, Callback callback) {
		this.ui = ui;
		this.user = user;
		this.callback = callback;

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
		    	UploadWindow upload = new UploadWindow(ui, user, callback);
		        ui.addWindow(upload);
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
