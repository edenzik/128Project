package com.wrangler.export;

import com.wrangler.constraint.ForeignKey;
import com.wrangler.load.Attribute;
import com.wrangler.load.Relation;

public class DDLMaker extends StatementMaker {

	public DDLMaker(Relation rel) {
		super(rel);
		statement.append(String.format("CREATE TABLE %s (", rel.getName()));
		statement.append("\n");
		for (Attribute att : rel.getAttributes()){
			statement.append(String.format("\t%s %s", att.getName(), att.getAttType()));
			for (ForeignKey fkey : rel.getFks()){
				statement.append(String.format("%s", att.getName(), att.getAttType()));
			}
			statement.append(",");
			statement.append("\n");
		}
		statement.deleteCharAt(statement.length()-2);		//Deletes the "," of the last attribute definition
		statement.append(");\n");
	}

}
