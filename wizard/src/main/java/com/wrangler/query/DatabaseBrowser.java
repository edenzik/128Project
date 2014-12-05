package com.wrangler.query;

import java.sql.SQLException;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.wrangler.login.User;

/**
 * @author edenzik
 *
 */
public class DatabaseBrowser extends HorizontalSplitPanel {
	private final TablesList tablesList;

	/**
	 * The main browser for browsing the database, includes a tables list
	 * on the left and the componenets to view a table on the right.
	 * @throws SQLException 
	 * @throws UnsupportedOperationException 
	 * 
	 */
	public DatabaseBrowser(User user){
		initLayout();
		tablesList = new TablesList(user.getDB());
		final QueryWindow window = new QueryWindow(user.getDB());
		addComponent(tablesList);
		addComponent(window);
		
		tablesList.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				window.displayQuery("SELECT * FROM " + event.getItem().getItemProperty("Table").getValue());
			}
		});
		setSizeFull();
	}
	
	private void initLayout(){
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
	}
	
	public void reload(){
		tablesList.reload();
	}

}
