package importer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.naming.ConfigurationException;

import beans.Review;
import config.ConfigurationManager;
import io.MongoDBHandler;

public class FileImporter {

	private MongoDBHandler mongo;
	private ConfigurationManager configuration;
	private BufferedReader reader;
	private static final String splitBy = ",";

	public FileImporter(ConfigurationManager configuration) throws ConfigurationException {
		this.mongo = new MongoDBHandler();
		this.configuration = configuration;
		String pathFile = this.configuration.getInputCsv();
		if (!pathFile.endsWith(".csv"))
			throw new ConfigurationException("Please put a valid csv file in the configuation");
		try {
			this.reader = new BufferedReader(new FileReader(pathFile));
		} catch (FileNotFoundException e) {
			System.err.println("Configuration file not found");
		}
	}

	public void importReviews() throws IOException {
		String line;
		int countLine = 0;
		while ((line = reader.readLine()) != null) {
			mongo.writeline(new Review(line.split(splitBy)));
			countLine++;
		}
		System.out.println("Number of reviews uploaded = " + countLine);
	}

}
