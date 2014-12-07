/**
 * 
 */
package com.wrangler.extract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.collections.IteratorUtils;

/**
 * @author edenzik
 *
 */
public class WrangledDataParser {
	private static final CSVFormat format = CSVFormat.EXCEL;

	/**
	 * @throws IOException 
	 * 
	 */
	public WrangledDataParser(String file) throws IOException {
		CSVParser parser = CSVParser.parse(file, format);
		List<CSVRecord> recordList = parser.getRecords();
		List<String> headers = IteratorUtils.toList(recordList.get(0).iterator());
		System.out.println("MAP");
		System.out.println(parser.getHeaderMap());
		System.out.println("NOW OTHER");
	
	}
	
	private String queryMaker(List<CSVRecord> records){
		
		
		return null;
		
	}
	

}
