/**
 * 
 */
package com.wrangler.ui.normalize;

import java.util.Set;

import com.wrangler.load.Database;
import com.wrangler.load.Relation;
import com.wrangler.normalization.Normalizer;
import com.wrangler.ui.query.TablesList;

/**
 * @author edenzik
 *
 */
class NormalizedTablesList extends TablesList {

	/**
	 * @param db
	 */
	public NormalizedTablesList(Database db) {
		super(db);
		initLayout();
		removeAllItems();
		
	}
	
	public void load(Set<Relation> relations){
		removeAllItems();
		try {
			for(Relation rel : relations) {
				addItem(new String[]{rel.getName(), rel.getAttributes().toString()}, null);
			}
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
	
	void initLayout(){
		addContainerProperty("Attributes", String.class, null);
		setSelectable(false);
	}

}
