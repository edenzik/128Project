/**
 * 
 */
package com.wrangler.ui.fd;

import java.sql.SQLException;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.UI;
import com.wrangler.fd.FDDetector;
import com.wrangler.load.RelationFactory;
import com.wrangler.load.TableNotFoundException;
import com.wrangler.ui.login.User;

/**
 * @author edenzik
 *
 */
public class FDPanel extends HorizontalSplitPanel {

	/**
	 * A panel containng the whole content of the window
	 * 
	 */
	public FDPanel(final User user) {
		initLayout();
		TableAttributeSelection tas = new TableAttributeSelection(user);
		final FDTable fds = new FDTable();
		final FDDetector detector = new FDDetector(user.getDB());

		ComboBox cbox = tas.getCbox();
		cbox.addValueChangeListener(new ComboBox.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue()!=null){
					fds.fill(detector.findAllHardFds(RelationFactory.createRelation(event.getProperty().getValue().toString(), user.getDB())));
				}

			}
		});
		addComponent(fds);
	}

	private void initLayout(){
		setSizeFull();
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
	}

}
