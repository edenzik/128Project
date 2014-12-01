package com.wrangler.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.AbstractContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.wrangler.fd.DBHelper;
import com.wrangler.fd.WrangledDataExtractor;
/**
 *
 * Created by edenzik on 11/27/14.
 */
/**
 *
 * Created by edenzik on 11/27/14.
 */
@Title("Relational Data Wrangler")
@Theme("valo")
public class Wizard extends UI
{

	@WebServlet(value = "/app/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Wizard.class)
	public static class Servlet extends VaadinServlet {
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(Wizard.class);

	private static final String HOST_IP = "104.236.17.70";
	private static final String HOST_PORT = "5432";
	private static final String DB_NAME = "cosi128a";
	private static final String DB_USER = "kahlil";
	private static final String DB_PASS = "psswd";
	private static final String WRANGLER_URI = "/VAADIN/wrangler/index.html";

	private Table contentList = new Table();
	private Table tablesList = new Table();
	private TextArea sqlField = new TextArea();
	private Button executeSqlButton = new Button("Run");

	private SimpleJDBCConnectionPool connectionPool = null;

	private SQLContainer container = null;
	private AbstractContainer tablesContainer = null;


	@Override
	protected void init(VaadinRequest request) {
		initIntroduction();
		initCSVUpload();

	}

	void initIntroduction(){
		Notification notification = new Notification("Welcome!",
				"The Relational Data Wrangler is a tool to turn a spreadsheet format into a relational format with ease.",
				Notification.Type.HUMANIZED_MESSAGE);
		notification.setDelayMsec(1000);
		notification.setPosition(Position.TOP_CENTER);
		notification.show(getPage());

	}

	void initCSVUpload(){
		Notification notification = new Notification("File Upload:",
				"Begin by uploading a CSV file to Wrangle.",
				Notification.Type.TRAY_NOTIFICATION);

		notification.setDelayMsec(10000);

		final OutputStream CSV = new ByteArrayOutputStream();

		VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {
			@Override
			public boolean handleRequest(VaadinSession session,
					VaadinRequest request,
					VaadinResponse response)
							throws IOException {
				if ("/csvUpload".equals(request.getPathInfo())) {
					response.setContentType("text/plain");
					response.getWriter().append(CSV.toString());
					return true; // We wrote a response
				} else {
					return false; // No response was written
				}
			}
		});

		Upload upload = new Upload("", 
				new Receiver(){
			public OutputStream receiveUpload(String filename,String mimeType) {
				return CSV;
			}
		});

		final Window window = getUploadWindow(upload);

		upload.addSucceededListener(
				new SucceededListener(){
					public void uploadSucceeded(SucceededEvent event) {
						window.close();
						initWrangler();
					}
				});

		upload.addFailedListener(new FailedListener(){
			public void uploadFailed(FailedEvent event){
				Notification.show("Upload Failed:",
						"Please refresh your browser and start again.",
						Notification.Type.ERROR_MESSAGE);
			}
		});
		addWindow(window);
	}

	Window getUploadWindow(Upload upload){
		final Window window = new Window("Upload CSV File");
		window.setClosable(false);
		window.setDraggable(false);
		window.setResizable(false);
		window.setHeight("20%");
		window.setWidth("30%");
		window.center();
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(upload);
		window.setContent(layout);
		return window;
	}

	void initWrangler(){
		addWindow(getWranglerWindow(WRANGLER_URI));
		//		RequestHandler handler = new RequestHandler(){
		//			public boolean handleRequest(VaadinSession session,
		//					VaadinRequest request,
		//					VaadinResponse response)
		//							throws IOException {
		//				if ("/hello".equals(request.getPathInfo())) {
		//					System.out.println("MAHH");
		//					loadData(request.getParameter("CHART_VALUE"));
		//					return true; // We wrote a response
		//				} else return false;
		//			}
		//		};
		//		VaadinSession.getCurrent().addRequestHandler(handler);	

		Notification notification = new Notification("",
				"The Data Wrangler step helps you conform your data to a spreadsheet like format, with every row containing exactly one data element.",
				Notification.Type.HUMANIZED_MESSAGE);
		notification.show(getPage());
		notification.setDelayMsec(1000);
		notification.setPosition(Position.BOTTOM_CENTER);
	}


	Window getWranglerWindow(String URI){
		final Window window = new Window("Data Wrangler");
		window.setHeight("90%");
		window.setWidth("90%");

		window.center();
		window.setDraggable(false);

		Button submit = new Button("Submit");

		submit.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				RequestHandler handler = new RequestHandler(){
					public boolean handleRequest(VaadinSession session,
							VaadinRequest request,
							VaadinResponse response)
									throws IOException {
						if ("/allDone".equals(request.getPathInfo())) {
							//System.out.println(request.getParameter("CHART_VALUE"));
							loadData(request.getParameter("CHART_VALUE"));
							session.removeRequestHandler(this);
							return true; // We wrote a response
						} else return false;
					}
				};
				VaadinSession.getCurrent().addRequestHandler(handler);

				Notification.show("Submitted!");

				window.close();
				initConnectionPool();
				initContentList();
				initTablesList();
				initLayout();

			}
		});


		GridLayout buttonLayout = new GridLayout();
		buttonLayout.addComponent(submit);
		buttonLayout.setComponentAlignment(submit, Alignment.TOP_CENTER);
		BrowserFrame browser = new BrowserFrame("",
				new ExternalResource(URI));
		VerticalSplitPanel layout = new VerticalSplitPanel(browser, buttonLayout);
		layout.setSplitPosition(90, Unit.PERCENTAGE);
		layout.setLocked(true);
		buttonLayout.setWidth("100%");
		buttonLayout.setHeight("100%");
		submit.setSizeUndefined();
		browser.setWidth("100%");
		browser.setHeight("100%");
		layout.setSplitPosition(90, Unit.PERCENTAGE);
		window.setContent(layout);

		return window;
	}

	//String tableValues = "";

	void loadData(String content){
		DBHelper db;
		try {
			db = new DBHelper(HOST_IP, DB_NAME,DB_USER,DB_PASS);
			WrangledDataExtractor wde = new WrangledDataExtractor(content, db);
			wde.createAndPopulateInitialTable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private Window teachWindow(){
		Window window = new Window("Moot");
		window.setHeight("50%");
		window.setWidth("50%");

		window.center();
		window.setClosable(false);
		window.setDraggable(false);

		window.setContent(new TextField("A Field"));

		return window;

	}



	private AbstractSplitPanel rightLayout(){
		VerticalSplitPanel sqlPanel = new VerticalSplitPanel();
		sqlPanel.setSplitPosition(80, Unit.PERCENTAGE);

		sqlPanel.addComponent(contentList);


		sqlPanel.addComponent(sqlExecutionFieldAndButton());

		contentList.setSizeFull();
		sqlField.setSizeFull();

		return sqlPanel;

	}

	private AbstractSplitPanel sqlExecutionFieldAndButton(){
		final HorizontalSplitPanel sqlSection = new HorizontalSplitPanel();
		sqlSection.setSplitPosition(90, Unit.PERCENTAGE);
		sqlSection.addComponent(sqlField);
		sqlSection.addComponent(executeSqlButton);
		executeSqlButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				runQuery(sqlField.getValue());
				initContentList();
			}
		});
		executeSqlButton.setSizeFull();
		return sqlSection;
	}

	private Table leftLayout(){


		tablesList.setSizeFull();
		tablesList.setImmediate(true);
		tablesList.setSelectable(true);
		tablesList.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				runQuery("SELECT * FROM " + event.getItem().getItemProperty("Table").getValue());
				initContentList();


			}
		});

		return tablesList;

	}

	private void initLayout() {
		HorizontalSplitPanel mainSplitPanel = new HorizontalSplitPanel();
		mainSplitPanel.setSplitPosition(20, Unit.PERCENTAGE);
		setContent(mainSplitPanel);

		mainSplitPanel.addComponent(leftLayout());
		mainSplitPanel.addComponent(rightLayout());

		//addWindow(uploadWindow());

		//addWindow(someWindow());

		//addWindow(wranglerWindow());


	}

	private void initContentList() {
		contentList.setContainerDataSource(container);
	}

	private void initTablesList() {
		try {
			tablesList.removeAllItems();
			ResultSet rs = connectionPool.reserveConnection().getMetaData().getTables(null, "public", null, new String[] {"TABLE"});
			tablesList.addContainerProperty("Table",String.class,null);
			while (rs.next()) tablesList.addItem(new String[]{rs.getString("TABLE_NAME")}, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tablesList.setSelectable(true);
		tablesList.setImmediate(true);
		//tablesList.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
	}






	private void initConnectionPool() {
		try {
			connectionPool = new SimpleJDBCConnectionPool(
					"org.postgresql.Driver",
					"jdbc:postgresql://" + HOST_IP + ":" + HOST_PORT + "/" + DB_NAME, DB_USER, DB_PASS);
		} catch (SQLException e) {
			showError("Couldn't create the connection pool!");
			e.printStackTrace();
		}
	}

	public void showError(String errorString) {
	}

	private void runQuery(String q) {
		try {
			FreeformQuery query = new FreeformQuery(q,connectionPool);
			container = new SQLContainer(query);
		} catch (SQLException e) {
			Notification.show("SQL Error!",
					e.getMessage(),
					Notification.Type.ERROR_MESSAGE);
		}
	}


}
