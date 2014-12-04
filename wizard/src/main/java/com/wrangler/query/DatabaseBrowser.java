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

/**
 * @author edenzik
 *
 */
public class DatabaseBrowser extends HorizontalSplitPanel {

	/**
	 * @throws SQLException 
	 * @throws UnsupportedOperationException 
	 * 
	 */
	public DatabaseBrowser(Database db){
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
		QueryWindow window = null;
		TablesList tables = new TablesList(db);
		try {
			window = new QueryWindow(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addComponent(tables);
		addComponent(window);
		
		tables.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//window.
				//initContentList();
			}
		});
		setSizeFull();
	}

}
