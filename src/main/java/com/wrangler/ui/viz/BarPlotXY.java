/**
 * 
 */
package com.wrangler.ui.viz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.vaadin.addon.charts.Chart;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.fd.FDDetector;
import com.wrangler.fd.FDFactory;
import com.wrangler.fd.FunctionalDependency;
import com.wrangler.fd.HardFD;
import com.wrangler.fd.SoftFD;
import com.wrangler.load.Attribute;
import com.wrangler.load.Database;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
import com.wrangler.ui.query.TablesList;

/**
 * @author edenzik
 *
 */
class BarPlotXY extends VerticalSplitPanel{

	/**
	 * 
	 */
	BarPlotXY(final TablesList tablesList, final Database db, final UI ui) {
		GridLayout grid = new GridLayout(4,1);
		initLayout();	
		
		final ComboBox selectY = new ComboBox();
		selectY.setInputPrompt("Y Axis");
		
		selectY.setWidth("40%");
		
		selectY.setHeight("10%");
		
		final ComboBox selectX = new ComboBox();
		selectX.setInputPrompt("X Axis");
		selectX.setWidth("40%");
		selectX.setHeight("10%");
		
		selectX.setNewItemsAllowed(true);
		
		grid.addComponent(selectX, 0, 0);
		
		grid.addComponent(selectY, 1, 0);
		
		Button add = new Button("Add");
		
		add.setHeight("10%");
		
		Button plot = new Button("Plot");
		
		plot.setHeight("10%");
		
		grid.addComponent(add, 2, 0);
		
		grid.addComponent(plot, 3, 0);
		
		add.setSizeFull();
		
		grid.setSpacing(true);
		
		
		
		plot.setSizeFull();
		
		
		
		final Chart chart = (new BasicColumn()).getChart();
		
		
		
		
		

		
		
		this.addComponents(grid, chart);
		chart.setSizeFull();
		
		setSizeFull();
		this.setSplitPosition(8, Unit.PERCENTAGE);
		setLocked(true);
		
		

		selectX.setSizeFull();
		selectY.setSizeFull();
		
		plot.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					System.out.println(selectX.getValue());
					Object sql = selectX.getValue();
					FreeformQuery query = new FreeformQuery(sql.toString(),db.getDbHelper().getPool());
					query.beginTransaction();
					ResultSet rs = query.getResults(0, 0);
					ArrayList<String> categories = new ArrayList<String>();
					while (rs.next()){
						categories.add(rs.getString(1));
						
						
						//System.out.println();
					}
					System.out.println(categories.toString());
					
					chart.getConfiguration().getxAxis().setCategories(categories.toArray(new String[categories.size()]));
					chart.drawChart();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			
		});
		
		
		
		
		tablesList.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.getItem().getItemProperty("Table").getValue().toString()!=null){
					selectX.removeAllItems();
					selectY.removeAllItems();
					for (Attribute att : db.getDbHelper().getRelationAttributes(RelationFactory.createExistingRelation(event.getItem().getItemProperty("Table").getValue().toString(), db))){
						selectX.addItem(att);
						selectY.addItem(att);
					}
				}

			}
		});





	}

	private void initLayout(){
		setSizeFull();
		//setSplitPosition(50, Unit.PERCENTAGE);
		//setLocked(true);
	}

}
