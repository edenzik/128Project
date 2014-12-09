/**
 * 
 */
package com.wrangler.ui.query;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Button.ClickEvent;
import com.wrangler.load.Database;

/**
 * @author edenzik
 *
 */
class TableListRemoveRename extends VerticalSplitPanel {
	private final TablesList tablesList;
	/**
	 * 
	 */
	public TableListRemoveRename(final Database db) {
		tablesList = new TablesList(db);
		initLayout();
		addComponent(tablesList);
		Button remove = new Button("Remove");
		remove.setSizeFull();
		addComponent(remove);
		
		remove.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				db.getDbHelper().executeUpdate("DROP TABLE " + tablesList.getItem(tablesList.getValue()) + ";");
				tablesList.reload();
			}
		});
	}
	
	private void initLayout(){
		setSplitPosition(90, Unit.PERCENTAGE);
		setLocked(true);
		setSizeFull();
	}
	
	TablesList getTablesList(){return tablesList;}

}
