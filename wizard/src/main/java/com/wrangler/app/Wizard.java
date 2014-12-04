package com.wrangler.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.AbstractContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.wrangler.app.tool.WranglerWindow;
import com.wrangler.app.upload.CSVUpload;
import com.wrangler.app.upload.UploadWindow;
import com.wrangler.extract.WrangledDataExtractor;
import com.wrangler.load.Database;
import com.wrangler.load.DatabaseFactory;
import com.wrangler.load.Host;
import com.wrangler.load.HostFactory;
import com.wrangler.login.User;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
/**
 *
 * Created by edenzik on 11/27/14.
 */
@Title("Relational Data Wrangler")
@Theme("valo")
public class Wizard extends UI
{

	private static final long serialVersionUID = 8261547005973362262L;

	@WebServlet(value = "/app", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Wizard.class)
	public static class Servlet extends VaadinServlet {
		private static final long serialVersionUID = 7869620340268929564L;
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(Wizard.class);
	
	private static final Host DEFAULT_HOST = HostFactory.createDefaultHost();

	private static String HOST_IP = "104.236.17.70";
	private static String HOST_PORT = "5432";
	private static String DB_NAME = "cosi128a";
	private static String HOST_ROLE = "kahlil";
	private static String HOST_PASS = "psswd";
	
	private User user;

	private Table contentList = new Table();
	private Table tablesList = new Table();

	private SimpleJDBCConnectionPool connectionPool = null;

	private SQLContainer container = null;
	private AbstractContainer tablesContainer = null;


	@Override
	protected void init(VaadinRequest request) {
		initLogin();
		//initCSVUpload();
	}
	
	void initLogin(){
		final LoginWindow login = new LoginWindow();
		addWindow(login);
		login.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				user = login.getUser();
				initUpload();
				initDatabaseBrowser();
			}
		});
	}
	
	void initUpload(){
		UploadWindow uploadWindow = new UploadWindow();
		addWindow(uploadWindow);
		final CSVUpload upload = uploadWindow.getUploader();
		upload.addSucceededListener(new Upload.SucceededListener() {
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				initWrangler();
			}
		});
		upload.addFailedListener(new Upload.FailedListener() {
			@Override
			public void uploadFailed(FailedEvent event) {
				initUpload();
			}
		});
		VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {
			@Override
			public boolean handleRequest(VaadinSession session,
					VaadinRequest request,
					VaadinResponse response)
							throws IOException {
				if ("/csvUpload".equals(request.getPathInfo())) {
					response.setContentType("text/plain");
					response.getWriter().append(upload.getOutputStream().toString());
					return true; // We wrote a response
				} else {
					return false; // No response was written
				}
			}
		});
	}

	void initWrangler(){
		final WranglerWindow window = new WranglerWindow();
		addWindow(window);
		window.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				System.out.println("POOPO");
				loadData(window.getWrangler().getResult().toString());
			}
		});
	}
	
	void initDatabaseBrowser(){
		initConnectionPool();
		initContentList();
		initLayout();
		initTablesList();
	}

	void loadData(String content){
		System.out.println("REACHED!!");
		try {
			Database db = DatabaseFactory.createDatabase(DB_NAME, DEFAULT_HOST);
			WrangledDataExtractor wde = new WrangledDataExtractor(content, db);
			wde.createAndPopulateInitialTable();
			initDatabaseBrowser();
		} catch (IOException e) {
			LOG.error("", e);
		} catch (ClassNotFoundException e) {
			LOG.error("", e);
		} catch (SQLException e) {
			LOG.error("", e);
		}
	}

	@Deprecated
	private void insert(CSVRecord record, String tableName){
		TableQuery query = new TableQuery(tableName, connectionPool);
		//query.storeRow(null, null, new String[]{record.))
	}
	
	@Deprecated
	private void runQuerys(String q) {
		try {
			FreeformQuery query = new FreeformQuery(q,connectionPool);
			container = new SQLContainer(query);
		} catch (SQLException e) {
			Notification.show("SQL Error!",
					e.getMessage(),
					Notification.Type.ERROR_MESSAGE);
		}
	}


	private Table leftLayout(){


		tablesList.setSizeFull();
		tablesList.setImmediate(true);
		tablesList.setSelectable(true);
		tablesList.addItemClickListener(new ItemClickListener() {

			private static final long serialVersionUID = 6889129017259570441L;

			@Override
			public void itemClick(ItemClickEvent event) {
				runQuery("SELECT * FROM " + event.getItem().getItemProperty("Table").getValue());
				initContentList();


			}
		});

		return tablesList;

	}

	private void initLayout() {
		VerticalSplitPanel mainSplitPanel = new VerticalSplitPanel();
		mainSplitPanel.setLocked(true);
		mainSplitPanel.setSplitPosition(5, Unit.PERCENTAGE);
		mainSplitPanel.addComponent(new MainMenu());
		mainSplitPanel.addComponent(new DatabaseBrowser());
		setContent(mainSplitPanel);

		//mainSplitPanel.addComponent(leftLayout());
		//mainSplitPanel.addComponent(new QueryWindow());
	}

	private void initContentList() {
		contentList.setContainerDataSource(container);
	}

	private void initTablesList() {
		try {
			tablesList.refreshRowCache();
			tablesList.removeAllItems();
			ResultSet rs = connectionPool.reserveConnection().getMetaData().getTables(null, "public", null, new String[] {"TABLE"});
			tablesList.addContainerProperty("Table",String.class,null);
			while (rs.next()) tablesList.addItem(new String[]{rs.getString("TABLE_NAME")}, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tablesList.setSelectable(true);
		tablesList.setImmediate(true);
	}






	private void initConnectionPool() {
		try {
			connectionPool = new SimpleJDBCConnectionPool(
					"org.postgresql.Driver",
					"jdbc:postgresql://" + HOST_IP + ":" + HOST_PORT + "/" + DB_NAME, HOST_ROLE, HOST_PASS);
		} catch (SQLException e) {
			showError("Couldn't create the connection pool!");
			e.printStackTrace();
		}
	}

	public void showError(String errorString) {
	}

	@Deprecated
	private void runQuery(String q) {
		try {
			FreeformQuery query = new FreeformQuery(q,connectionPool);
			container = new SQLContainer(query);
		} catch (SQLException e) {
			LOG.warn("", e);
			Notification.show("SQL Error!",
					e.getMessage(),
					Notification.Type.ERROR_MESSAGE);
		}
	}


}
