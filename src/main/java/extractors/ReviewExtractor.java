package extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beans.Review;
import config.ConfigurationManager;
import crawler.PlayStoreCrawler;
import csv.CSVWriter;

public class ReviewExtractor extends Extractor {

	private Date dateOfLastCrawl = null;
	private ConfigurationManager configurationManager;

	public ReviewExtractor(ArrayList<String> appsToMine, ConfigurationManager configurationManager) {
		super(appsToMine);
		this.configurationManager = configurationManager;
	}

	@Override
	public void extract() {
		
		dateOfLastCrawl = configurationManager.getDateOfLastCrawl();
		int noThreads = configurationManager.getNumberOfThreadToUse();
		int numberOfApps = this.appsToMine.size();
		int innerLoop = numberOfApps/noThreads;

		for (int i = 0; i < noThreads; i++) {
			for (int j = 0; j < innerLoop; j++) {
				
				boolean inBounds = ((i+j+1) >= 0) && ((i+j+1) < this.appsToMine.size());
				
				if (!inBounds)
					break;
				
				final String currentApp = this.appsToMine.get(i+j+1);
	            new Thread(new Runnable() {
	                public void run() {
	                    PlayStoreCrawler googlePlayStoreCrawler = new PlayStoreCrawler(currentApp, dateOfLastCrawl);

	                    List<Review> googleReviews = googlePlayStoreCrawler.getReviews();
	                    for (Review review : googleReviews) {
							try {
								CSVWriter.writeline(review.getFieldsToExport());
							} catch (IOException e) {
								System.err.println("An error occurred during the export of a review");
							}
						}
	                }
	            }).start();
	            
			}
		}
		
		try {
			ConfigurationManager.getInstance().updateDateOfLastCrawl();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}