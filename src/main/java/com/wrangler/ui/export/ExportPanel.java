/**
 * 
 */
package com.wrangler.ui.export;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.wrangler.export.ContentMaker;
import com.wrangler.export.DDLMaker;
import com.wrangler.load.Relation;
import com.wrangler.load.RelationFactory;
import com.wrangler.ui.login.User;
import com.wrangler.ui.query.TablesList;

/**
 * @author edenzik
 *
 */
class ExportPanel extends HorizontalSplitPanel {

	/**
	 * 
	 */
	public ExportPanel(final User user) {
		initLayout();
		
		//Set<Relation > relations = user.getDB().getDbHelper().getRelations();
		
		final TablesList tablesList = new TablesList(user.getDB());
		VerticalLayout tablesListLayout = new VerticalLayout(tablesList);
		tablesListLayout.setMargin(new MarginInfo(true, false, false, false));
		
		final Button export = new Button("Download");
		
		
        
		export.setSizeFull();
		
		VerticalSplitPanel tablesListExportButton = new VerticalSplitPanel(tablesListLayout, export);
		tablesListExportButton.setSizeFull();
		tablesListExportButton.setSplitPosition(90);
		tablesListExportButton.setLocked(true);
		
		final TextArea sqlExport = new TextArea();
		
		tablesList.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				sqlExport.setReadOnly(false);
				String relationName = (String) event.getItem().getItemProperty("Table").getValue();
				DDLMaker sm = new DDLMaker(RelationFactory.createExistingRelation(relationName, user.getDB()));
				ContentMaker cm = new ContentMaker(RelationFactory.createExistingRelation(relationName, user.getDB()));
				sqlExport.setValue(sm.getStatement() + "\n" + cm.getStatement());
				sqlExport.setReadOnly(true);
				
				Resource res = new StreamResource(new StreamResource.StreamSource() {
					
					@Override
					public InputStream getStream() {
						try {
							return IOUtils.toInputStream(sqlExport.getValue(), "UTF-8");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
				}, relationName + ".sql");
				if (export.getExtensions().size() != 0) export.removeExtension(export.getExtensions().iterator().next());
				FileDownloader fd = new FileDownloader(res);
				fd.setFileDownloadResource(res);
				fd.extend(export);
			}
		});
		
		addComponent(tablesListExportButton);
		VerticalLayout sqlExportFieldLayout = new VerticalLayout(sqlExport);
		sqlExportFieldLayout.setMargin(new MarginInfo(true, false, false, false));
		addComponent(sqlExportFieldLayout);
		
		sqlExport.setSizeFull();
		sqlExportFieldLayout.setSizeFull();

	}

	private void initLayout(){
		setSizeFull();
		setSplitPosition(20, Unit.PERCENTAGE);
		setLocked(true);
	}
}
