/**
 * 
 */
package com.wrangler.ui.query;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Button.ClickEvent;
import com.wrangler.load.Database;
import com.wrangler.load.RelationFactory;
import com.wrangler.ui.query.SelectedItem;

/**
 * @author edenzik
 *
 */
class TableListRemoveRename extends VerticalSplitPanel {
	private final TablesList tablesList;
	private final ComboBox cbox;
	/**
	 * 
	 */
	public TableListRemoveRename(final Database db) {
		tablesList = new TablesList(db);
		initLayout();
		addComponent(tablesList);
		
		this.cbox = new ComboBox();
		//Button remove = new Button("Remove");
		
		
		//remove.setSizeFull();
		//addComponent(remove);
		
		addComponent(cbox);
		cbox.setSizeFull();
		
		
		class removeTable extends SelectedItem{

			@Override
			public void execute() {
				db.getDbHelper().executeUpdate("DROP TABLE " + tablesList.getItem(tablesList.getValue()) + ";");
				tablesList.reload();
				
			}
			@Override
			public String toString() {
				return "Remove Table";
			}
		}
		
		class sumTable extends SelectedItem{

			@Override
			public void execute() {
				db.getDbHelper().executeUpdate("DROP TABLE " + tablesList.getItem(tablesList.getValue()) + ";");
				
				tablesList.reload();
				
			}
			@Override
			public String toString() {
				return "Sum Table";
			}
		}
		
		cbox.addItem(new sumTable());
		
		cbox.addValueChangeListener(new ComboBox.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue()!=null){
					((SelectedItem) event.getProperty().getValue()).execute();
				}

			}
		});
		

	}
	
	private void initLayout(){
		setSplitPosition(90, Unit.PERCENTAGE);
		setLocked(true);
		setSizeFull();
	}
	
	TablesList getTablesList(){return tablesList;}
	
	ComboBox getSelect(){return cbox;}

}
