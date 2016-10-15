package extractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import config.ConfigurationManager;
import crawler.Crawler;
import crawler.CrawlerFactory;

public class ReviewExtractor extends Extractor {

	private Date dateOfLastCrawl = null;
	private ConfigurationManager configurationManager;
	private String store;

	public ReviewExtractor(ArrayList<String> appsToMine, ConfigurationManager configurationManager) {
		super(appsToMine);
		this.configurationManager = configurationManager;
	}

	@Override
	public void extract() {

		dateOfLastCrawl = configurationManager.getDateOfLastCrawl();
		store = this.configurationManager.getStoreToCrawl();

		ExecutorService executor = Executors.newFixedThreadPool(this.appsToMine.size());

		try {
			ConfigurationManager.getInstance().updateDateOfLastCrawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		for (String currentApp : appsToMine) {
			Crawler googlePlayStoreCrawler = CrawlerFactory.getCrawler(store, currentApp, dateOfLastCrawl);
			executor.execute(googlePlayStoreCrawler);
		}
		executor.shutdown();
		
	}

}
