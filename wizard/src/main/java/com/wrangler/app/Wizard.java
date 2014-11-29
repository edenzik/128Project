package com.wrangler.app;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import com.vaadin.ui.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.annotations.Title;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.data.util.AbstractContainer;
/**
 *
 * Created by edenzik on 11/27/14.
 */
@Title("Relational Data Wrangler")
@Theme("valo")
public class Wizard extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = Wizard.class, widgetset = "com.wrangler.app.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private Table contentList = new Table();
    private Table tablesList = new Table();
    private TextArea sqlField = new TextArea();
    private Button executeSqlButton = new Button("Run");
    final ProgressBar bar = new ProgressBar();

    private SimpleJDBCConnectionPool connectionPool = null;

    private SQLContainer container = null;
    private AbstractContainer tablesContainer = null;


    @Override
    protected void init(VaadinRequest request) {
        initConnectionPool();
        initContentList();
        initTablesList();
        initLayout();
        Notification.show("Welcome!",
                "Please select a table from the left.",
                Notification.Type.HUMANIZED_MESSAGE);
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


    }

    private void initLayout_old() {

		/* Root of the user interface component tree is set */
        HorizontalSplitPanel mainSplitPanel = new HorizontalSplitPanel();

        mainSplitPanel.setSplitPosition(20, Unit.PERCENTAGE);
        setContent(mainSplitPanel);

        //Make layout panels
        VerticalLayout rightLayout = new VerticalLayout();
        VerticalLayout leftLayout = new VerticalLayout();




        //Make right panel
        rightLayout.addComponent(contentList);


        leftLayout.addComponent(tablesList);
        tablesList.setWidth("100%");
        tablesList.setHeight("100%");

        contentList.setSizeFull();


        //contentList.setWidth("100%");


        HorizontalLayout bottomRightLayout = new HorizontalLayout();
        rightLayout.addComponent(bottomRightLayout);
        bottomRightLayout.setWidth("100%");
        bottomRightLayout.addComponent(sqlField);
        sqlField.setWidth("100%");
        bottomRightLayout.addComponent(executeSqlButton);
        executeSqlButton.setHeight("100%");
        bottomRightLayout.setExpandRatio(sqlField, 1);

        rightLayout.setExpandRatio(contentList, 1);

        leftLayout.setExpandRatio(tablesList, 1);
        leftLayout.setSizeFull();
        rightLayout.setSizeFull();  //Use all the space

        // Add both components
        mainSplitPanel.addComponent(leftLayout);
        mainSplitPanel.addComponent(rightLayout);

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
                    "jdbc:postgresql://wrangler.cqt8za2u22th.us-east-1.rds.amazonaws.com:5432/wrangler", "edenzik", "superglue1994");
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
