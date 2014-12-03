/**
 * 
 */
package com.wrangler.app;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.wrangler.login.IncorrectPasswordException;
import com.wrangler.login.LoginManager;
import com.wrangler.login.User;
import com.wrangler.login.UserNotFoundException;

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
		FormLayout form = new FormLayout();
		final TextField nameField = new TextField("Email");
		form.addComponent(nameField);
	    final TextField passwordField = new TextField("Password");
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IncorrectPasswordException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    });
	    
	    
	    setClosable(false);
		setDraggable(false);
		setResizable(false);
		setHeight("28%");
		setWidth("24%");
		center();
	    
	    setContent(form);
	}
	
	User getUser(){return user;}
}
