/**
 * 
 */
package com.wrangler.app.tool;

import java.io.IOException;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * @author edenzik
 *
 */
public class WranglerWindow extends Window {
	
	private final DataWrangler wrangler;
	

	/**
	 * 
	 */
	public WranglerWindow() {
		super("Data Wrangler");
		setHeight("90%");
		setWidth("90%");
		setDraggable(false);
		center();
		
		Button submit = new Button("Submit");
		submit.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				wrangler.setRead();
				if (wrangler.isDone()) close();
			}
		});
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.addComponent(submit);
		buttonLayout.setComponentAlignment(submit, Alignment.TOP_CENTER);
		wrangler = new DataWrangler();
		VerticalSplitPanel layout = new VerticalSplitPanel(wrangler, buttonLayout);
		layout.setSplitPosition(90, Unit.PERCENTAGE);
		layout.setLocked(true);
		buttonLayout.setWidth("100%");
		buttonLayout.setHeight("100%");
		submit.setHeight(50, Unit.PERCENTAGE);
		submit.setSizeUndefined();
		wrangler.setWidth("100%");
		wrangler.setHeight("100%");
		layout.setSplitPosition(90, Unit.PERCENTAGE);
		setContent(layout);
	}
	
	public DataWrangler getWrangler(){return wrangler;}

}
