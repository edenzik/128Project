package com.wrangler.fd;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVTest {

	public static void main(String[] args) throws IOException {
		CSVParser parser = CSVParser.parse(new File("test.csv"), Charset.defaultCharset(), CSVFormat.DEFAULT);
		Iterator<CSVRecord> i = parser.iterator();
		for(int j = 0; j < 5; ++j) {
			System.out.println(i.next());
		}
	}

}
