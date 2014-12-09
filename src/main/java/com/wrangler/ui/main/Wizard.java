package com.wrangler.ui.main;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.wrangler.load.Host;
import com.wrangler.load.HostFactory;
import com.wrangler.ui.login.LoginWindow;
/**
 *
 * Created by edenzik on 11/27/14.
 */
@Title("Schematic Fission")
@Theme("mytheme")
public class Wizard extends UI {
	private static final long serialVersionUID = 8261547005973362262L;

	@WebServlet(value = "/app", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Wizard.class)
	public static class Servlet extends VaadinServlet {
		private static final long serialVersionUID = 7869620340268929564L;
	}

	private static final Logger LOG = LoggerFactory.getLogger(Wizard.class);

	private static final Host DEFAULT_HOST = HostFactory.createDefaultHost();

	/* 
	 * Initializes the login window, which begins a stream of UI elements 
	 * to open - this is done by passing this UI to LoginWindow.
	 * 
	 * Sets the background:
	 * defined in src/main/java/webapp/VAADIN/themes/mytheme/mytheme.scss
	 * 
	 * (non-Javadoc)
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) {
		setStyleName("backgroundimage");
		addWindow(new LoginWindow(this));
	}

}