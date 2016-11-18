package extractors;

import java.util.ArrayList;
import java.util.List;

import beans.Review;
import config.ConfigurationManager;
import io.CSVWriter;
import io.MongoDBWriter;

public class DatabaseExtractor extends Extractor {
	
	private MongoDBWriter mongo = new MongoDBWriter();
	private CSVWriter csvWriter = new CSVWriter();

	public DatabaseExtractor(ArrayList<String> inputApps, ConfigurationManager config) {
		super(inputApps);
	}

	@Override
	public void extract() {
		
		for (String app : appsToMine) {
			List<Review> reviews = mongo.getReviewsFromDB(app);
			this.writeReviewForAnApp(reviews);
		}
	}

	private void writeReviewForAnApp(List<Review> reviews) {
		for (Review review : reviews) {
			this.csvWriter.writeline(review);
		}
	}
}
