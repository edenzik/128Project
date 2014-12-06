/**
 * 
 */
package com.wrangler.wranglertool;

import java.io.IOException;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.wrangler.login.User;

/**
 * @author edenzik
 *
 */
public class WranglerWindow extends Window {
	
	private final DataWrangler wrangler;
	

	/**
	 * This is the actual window for data wrangler
	 * Adds button to close this window when the CSV is done uploading
	 * 
	 */
	public WranglerWindow(final UI ui, final User user) {
		super("Data Wrangler");
		initLayout();
		
		final Button submit = new Button("Load");

		GridLayout buttonLayout = new GridLayout();
		submit.setSizeFull();
		submit.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				wrangler.setReady();
				submit.setCaption("Done");
				if (wrangler.isDone()) {
					close();
				}
				
			}
		});
		buttonLayout.addComponent(submit);
		buttonLayout.setComponentAlignment(submit, Alignment.TOP_CENTER);
		wrangler = new DataWrangler(ui, user);
		VerticalSplitPanel layout = new VerticalSplitPanel(wrangler, buttonLayout);
		layout.setSplitPosition(90, Unit.PERCENTAGE);
		layout.setLocked(true);
		buttonLayout.setWidth("100%");
		buttonLayout.setHeight("100%");
		wrangler.setWidth("100%");
		wrangler.setHeight("100%");
		layout.setSplitPosition(90, Unit.PERCENTAGE);
		setContent(layout);
	}
	
	private void initLayout(){
		setHeight("90%");
		setWidth("90%");
		setDraggable(false);
		center();
	}

}
