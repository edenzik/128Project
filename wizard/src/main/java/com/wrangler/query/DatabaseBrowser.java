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
		initLayout();
		TablesList tables = new TablesList(db);
		final QueryWindow window = new QueryWindow(db);
		addComponent(tables);
		addComponent(window);
		
		tables.addItemClickListener(new ItemClickListener() {
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

}
