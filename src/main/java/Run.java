import java.io.IOException;

import config.ConfigurationManager;
import csv.CSVReader;
import extractors.Extractor;
import extractors.ReviewExtractor;

public class Run {

    public  static void main(String[] args){

    	CSVReader reader = new CSVReader(null);
		ConfigurationManager config = null;
		try {
			config = ConfigurationManager.getInstance();
		} catch (IOException e) {
			System.err.println("An error occurred while loading the configuration");
		}
    	
    	Extractor extractor = new ReviewExtractor(reader.getAppList(), config);
    	extractor.extract();
    }
}
