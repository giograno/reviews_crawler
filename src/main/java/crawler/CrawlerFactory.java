package crawler;

import java.util.ArrayList;

import config.ConfigurationManager;

public class CrawlerFactory {
	
	public static final String APPLE_STORE = "apple";
	public static final String PLAY_STORE  = "google";
	public static final String GOOGLE_INFO = "playInfo";
	public static final String APPLE_INFO  = "appleInfo";
	
	public static Crawler getCrawler(ConfigurationManager config, ArrayList<String> appToMine, String whichCrawler) {
		
		if (whichCrawler.equalsIgnoreCase(PLAY_STORE))
			return new GoogleReviewsCrawler(appToMine.get(0), config);
		else if (whichCrawler.equalsIgnoreCase(GOOGLE_INFO))
			return new GoogleInfoCrawler(appToMine);
		else 
			throw new RuntimeException("No crawler implemeted for " + whichCrawler);
	}
}
