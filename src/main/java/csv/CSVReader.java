package csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import config.ConfigurationManager;

public class CSVReader {
	
	private BufferedReader reader;
	private String splitBy = ",";
	private ArrayList<String> appList;
	
	public CSVReader(String spliBy) {
		if (spliBy != null) 
			this.splitBy = spliBy;
		this.appList = new ArrayList<>();
		try {
			this.extractAppList();
		} catch (Exception e) {
			System.err.println("Something wrong happens while reading input file");
		} 
	}
	
	private void extractAppList() throws FileNotFoundException, IOException {
		String line;
		this.reader = new BufferedReader(new FileReader(
						ConfigurationManager.getInstance().getInputCsv()));
		while ((line = reader.readLine()) != null) {
			String[] auxLine = line.split(this.splitBy);
			this.appList.add(auxLine[0]);
		}
	}
	
	/**
	 * Returns the list of the APPS to be mined 
	 * @return
	 */
	public ArrayList<String> getAppList() {
		return this.appList;
	}
	
	
	
}
