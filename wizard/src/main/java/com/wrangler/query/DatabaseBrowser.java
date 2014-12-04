/**
 * 
 */
package com.wrangler.query;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.load.Database;
import com.wrangler.login.User;

/**
 * @author edenzik
 *
 */
public class DatabaseBrowser extends HorizontalSplitPanel {
	private final TablesList tablesList;

	/**
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
