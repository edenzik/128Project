package com.wrangler.app;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.wrangler.load.Host;
import com.wrangler.load.HostFactory;
import com.wrangler.login.LoginWindow;
import com.wrangler.login.User;
import com.wrangler.query.DatabaseBrowser;
import com.wrangler.upload.UploadWindow;
import com.wrangler.wranglertool.WranglerWindow;
/**
 *
 * Created by edenzik on 11/27/14.
 */
@Title("Relational Data Wrangler")
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

	private User user;

	@Override
	protected void init(VaadinRequest request) {
		initLogin();
	}

	void initLogin() {
		final LoginWindow login = new LoginWindow();
		addWindow(login);
		login.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				user = login.getUser();
				initLayout();
			}
		});
	}

	void initUpload() {
		UploadWindow uploadWindow = new UploadWindow();
		addWindow(uploadWindow);
		uploadWindow.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				initWrangler();
			}
		});
	}

	void initWrangler() {
		final WranglerWindow window = new WranglerWindow();
		addWindow(window);
		window.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				try {
					window.getWrangler().loadData(user.getDB());
				} catch (IOException e1) {
					LOG.error("", e);
					Notification.show("Failed to read. Please try again.",
							e1.getMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
		});
	}

	private void initLayout() {
		VerticalSplitPanel mainSplitPanel = new VerticalSplitPanel();
		mainSplitPanel.setLocked(true);
		mainSplitPanel.setSplitPosition(5, Unit.PERCENTAGE);
		mainSplitPanel.addComponent(new MainMenu());
		mainSplitPanel.addComponent(new DatabaseBrowser(user.getDB()));
		setContent(mainSplitPanel);
	}
}