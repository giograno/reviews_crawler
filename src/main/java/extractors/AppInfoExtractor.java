package extractors;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import config.ConfigurationManager;
import crawler.Crawler;
import crawler.CrawlerFactory;

public class AppInfoExtractor extends Extractor {

	private ConfigurationManager configurationManager;

	public AppInfoExtractor(ArrayList<String> inputApps, ConfigurationManager configurationManager) {
		super(inputApps);
		this.configurationManager = configurationManager;
	}

	@Override
	public void extract() {
		ExecutorService executor = Executors.newFixedThreadPool(this.configurationManager.getNumberOfThreadToUse());

		Crawler googlePlayStoreCrawler = CrawlerFactory.getCrawler(this.configurationManager, this.appsToMine, "playInfo");
		
		Future<Integer> future = executor.submit(googlePlayStoreCrawler);
		try {
			future.get();
		} catch (Exception e) {
			System.err.println("It was not possibile to complete the extraction");
		} 
		executor.shutdown();
	}

}
