package crawler;

import java.util.Date;

public class CrawlerFactory {
	
	public static final String APPLE_STORE = "apple";
	public static final String PLAY_STORE  = "google";
	
	public static Crawler getCrawler(String store, String appToMine, Date dateFrom) {
		if (store.equalsIgnoreCase(PLAY_STORE))
			return new PlayStoreCrawler(appToMine, dateFrom);
		else 
			throw new RuntimeException("Wrong store name: " + store);
	}

}
