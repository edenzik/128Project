/**
 * 
 */
package com.wrangler.ui.login;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.wrangler.ui.main.MainMenu;
import com.wrangler.ui.query.DatabaseBrowser;

/**
 * @author edenzik
 *
 */
public class LoginWindow extends Window {


	/**
	 * A window spawned at start that lets the user log in.
	 * 
	 * Initializes all the componenets, if login is correct permits user
	 * to proceed and opens up the main window.
	 * @param the current user interface to which we add subsequent windows.
	 * 
	 */
	public LoginWindow(final UI ui) {
		super("Please Login");
		initLayout();
		FormLayout form = new FormLayout();
		final TextField nameField = new TextField("Username");
		form.addComponent(nameField);
		final PasswordField passwordField = new PasswordField("Password");
		form.addComponent(passwordField);
		Button submit = new Button("Login");
		form.addComponent(submit);
		form.setMargin(true);
		
		//Click listener on the submit user interface button
		submit.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					LoginManager lm = new LoginManager();	//Logs in the user
					User user = lm.login(nameField.getValue(), passwordField.getValue());
					close();
					next(ui, user);						//Initializes the actual window
				} catch (UserNotFoundException e) {
					Notification.show("User not found!",
							e.getMessage(),
							Notification.Type.ERROR_MESSAGE);
				} catch (IncorrectPasswordException e) {
					Notification.show("Invalid password!",
							e.getMessage(),
							Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		setContent(form);
	}
	
	private void initLayout(){
		setClosable(false);
		setDraggable(false);
		setResizable(false);
		setHeight("28%");
		setWidth("24%");
		center();
	}
	
	/**
	 * Initializes the next components of the UI - including the query browser
	 * and the main menu.
	 * 
	 * @param the main ui and the user to pass on
	 * 
	 */
	private void next(UI ui, User user){
		VerticalSplitPanel mainSplitPanel = new VerticalSplitPanel();
		mainSplitPanel.setLocked(true);
		mainSplitPanel.setSplitPosition(5, Unit.PERCENTAGE);
		DatabaseBrowser browser = new DatabaseBrowser(user);
		mainSplitPanel.addComponent(new MainMenu(ui, user));
		mainSplitPanel.addComponent(browser);
		ui.setContent(mainSplitPanel);
	}
}
