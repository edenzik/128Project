package com.wrangler.export;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.wrangler.load.Attribute;
import com.wrangler.load.Relation;

public class ContentMaker extends StatementMaker {

	public ContentMaker(Relation rel) {
		super(rel);
		statement.append(String.format("INSERT INTO %s (", rel.getName()));
		for (Attribute att : rel.getAttributes()){
			statement.append(String.format("%s, ", att.getName()));	
		}
		statement.deleteCharAt(statement.length()-1);
		statement.append(") VALUES");
		statement.append("\n");
		try {
			statement.append(resultSetToStringBuilder(rel.getSourceDb().getDbHelper().executeQuery(String.format("SELECT * FROM %s", rel.getName()))));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		statement.deleteCharAt(statement.length()-1);		//Deletes the "\n" of the last insertion
		statement.deleteCharAt(statement.length()-1);		//Deletes the "," of the last insertion
		statement.append(";");
	}
	
	private StringBuilder resultSetToStringBuilder(ResultSet data) throws SQLException{
		StringBuilder values = new StringBuilder();
		while (data.next()) {
			StringBuilder line = new StringBuilder();
			line.append("\t(");
	        for (int i = 1; i<=data.getMetaData().getColumnCount(); i++){
	        	line.append(String.format("'%s', ", data.getString(i)));
	        }
	        line.deleteCharAt(line.length()-2);
	        line.append("),\n");
	        values.append(line);
	    }
		return values;
	}

}
