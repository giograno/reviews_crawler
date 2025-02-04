package extractors;

import java.util.ArrayList;

import config.ConfigurationManager;

public class ExtractorFactory {

	private final static String REVIEWS = "reviews";
	private final static String INFO 	= "info";
	private final static String EXPORT	= "export";

	public static Extractor getExtractor(ArrayList<String> appsToMine, ConfigurationManager configurationManager,
			String whichExtractor) {

		if (whichExtractor.equalsIgnoreCase(REVIEWS))
			return new ReviewExtractor(appsToMine, configurationManager);
		else if (whichExtractor.equalsIgnoreCase(INFO))
			return new AppInfoExtractor(appsToMine, configurationManager);
		else if (whichExtractor.equalsIgnoreCase(EXPORT))
			return new DatabaseExtractor(appsToMine, configurationManager);
		else
			throw new RuntimeException("No implemented extractor for " + whichExtractor);
	}
}
