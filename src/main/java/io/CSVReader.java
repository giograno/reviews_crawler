package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import config.ConfigurationManager;

/**
 * Reads the csv input file and extracts the input files
 * @author grano
 *
 */
public class CSVReader extends AppListReader {

	public CSVReader(String spliBy) {
		super(spliBy);
	}
	
	protected void extractAppList() throws FileNotFoundException, IOException {
		String line;
		this.reader = new BufferedReader(new FileReader(
						ConfigurationManager.getInstance().getInputCsv()));
		while ((line = reader.readLine()) != null) {
			String[] auxLine = line.split(this.splitBy);
			this.appList.add(auxLine[0]);
		}
	}	
}
