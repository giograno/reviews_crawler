package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public abstract class AppListReader {
	
	protected BufferedReader reader;
	protected String splitBy = ",";
	protected ArrayList<String> appList;
	
	public AppListReader(String splitBy) {
		if (splitBy != null)
			this.splitBy = splitBy;
		this.appList = new ArrayList<>();
		try {
			this.extractAppList();
		} catch (Exception e) {
			System.err.println("Something wrong happens while reading input file");
		} 
	}

	protected abstract void extractAppList() throws FileNotFoundException, IOException;

	/**
	 * Returns the list of the APPS to be mined 
	 * @return
	 */
	public ArrayList<String> getAppList() {
		return this.appList;
	}
}
