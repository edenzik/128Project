/**
 * 
 */
package com.wrangler.login;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 * @author edenzik
 *
 */
public class LoginWindow extends Window {
	private User user = null;


	/**
	 * 
	 */
	public LoginWindow() {
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
		submit.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					LoginManager lm = new LoginManager();
					user = lm.login(nameField.getValue(), passwordField.getValue());
					
					close();
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

	public User getUser(){return user;}
}
