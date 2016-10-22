package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import config.ConfigurationManager;

public class TxtReader extends AppListReader {

	public TxtReader(String splitBy) {
		super(splitBy);
	}

	@Override
	protected void extractAppList() throws FileNotFoundException, IOException {
		this.reader = new BufferedReader(new FileReader(
				ConfigurationManager.getInstance().getInputCsv()));
		
		String auxLine;
		while ((auxLine = reader.readLine()) != null) {
			String appName = auxLine.split(this.splitBy)[0];
			if (appName.contains("_"))
				this.appList.add(appName.split("_")[0]);
			else 
				this.appList.add(appName);
		}
	}
	
	

}
