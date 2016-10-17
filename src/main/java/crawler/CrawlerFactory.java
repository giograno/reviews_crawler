package crawler;

import java.util.Date;

import config.ConfigurationManager;
import crawler.info.PlayStoreCrawlerInfo;

public class CrawlerFactory {
	
	public static final String APPLE_STORE = "apple";
	public static final String PLAY_STORE  = "google";
	public static final String GOOGLE_INFO = "playInfo";
	public static final String APPLE_INFO  = "appleInfo";
	
	public static Crawler getCrawler(ConfigurationManager config, String appToMine, String whichCrawler) {
		Date dateOfLastCrawl = config.getDateOfLastCrawl();
		String store = config.getStoreToCrawl();
		
		if (store.equalsIgnoreCase(PLAY_STORE))
			return new PlayStoreCrawler(appToMine, dateOfLastCrawl);
		else if (store.equalsIgnoreCase(GOOGLE_INFO))
			return new PlayStoreCrawlerInfo(config);
		else 
			throw new RuntimeException("No crawler implemeted for " + whichCrawler);
	}
}
