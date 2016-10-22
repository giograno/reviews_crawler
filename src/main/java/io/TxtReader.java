package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.ConfigurationManager;

public class TxtReader extends AppListReader {

	public TxtReader(String splitBy) {
		super(splitBy);
	}

	@Override
	protected void extractAppList() throws FileNotFoundException, IOException {
		this.reader = new BufferedReader(new FileReader(ConfigurationManager.getInstance().getInputCsv()));

		String auxLine;
		while ((auxLine = reader.readLine()) != null) {
			String appName = auxLine.split(this.splitBy)[0];

			Pattern pattern = Pattern.compile("^(.+?)_[1-9]");
			Matcher matcher = pattern.matcher(appName);
			if (matcher.find()) {
				String aux = matcher.group(1);
				if (!this.appList.contains(aux))
					this.appList.add(aux);
			}
			else
				System.err.println("Not valid pattern name for: " + appName);
		}
	}

}
