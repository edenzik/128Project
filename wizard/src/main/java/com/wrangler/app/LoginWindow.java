/**
 * 
 */
package com.wrangler.app;

import java.sql.SQLException;

import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.wrangler.login.User;

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
					user = new User(nameField.getValue(), passwordField.getValue());
					close();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
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
