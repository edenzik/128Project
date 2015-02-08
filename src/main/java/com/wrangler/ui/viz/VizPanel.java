/**
 * 
 */
package com.wrangler.ui.viz;

import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
import com.wrangler.ui.login.User;
import com.wrangler.ui.query.TablesList;

/**
 * @author edenzik
 *
 */
class VizPanel extends HorizontalSplitPanel {

	/**
	 * A panel containng the whole content of the window
	 * 
	 */
	VizPanel(UI ui, final User user) {
		initLayout();
		
		Set<Relation> relations = user.getDB().getDbHelper().getRelations();
		

	

		
		TablesList tablesList = new TablesList(user.getDB());
		
		final ComboBox chartType = new ComboBox();
		chartType.setInputPrompt("Select Chart Type");
		chartType.setSizeFull();
		
	
		VerticalSplitPanel chartTypeTableSelection = new VerticalSplitPanel(chartType, tablesList);
		chartTypeTableSelection.setSizeFull();
		chartTypeTableSelection.setLocked(true);
		chartTypeTableSelection.setSplitPosition(8, Unit.PERCENTAGE);

		addComponent(chartTypeTableSelection);
		
		VerticalSplitPanel chartXYSelection = new VerticalSplitPanel();
		addComponent(new BarPlotXY(tablesList, user.getDB(), ui));
		//chartXYSelection.addComponent((new BasicColumn()).getChart());
		
		//chartXYSelection.setSplitPosition(8, Unit.PERCENTAGE);
		
		
		//addComponent(chartXYSelection);
	}

	private void initLayout(){
		setSizeFull();
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
	}

}
