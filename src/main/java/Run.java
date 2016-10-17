import java.io.IOException;

import config.ConfigurationManager;
import csv.CSVReader;
import extractors.Extractor;
import extractors.ExtractorFactory;

public class Run {

	private static final int RUNNER_REVIEW = 0;
	private static final int RUNNER_INFO = 1;

	private int runnerType;

	public static void main(String[] args) throws Exception {

		Run runner = new Run(args);
		runner.run();
	}

	public Run(String[] args) {

		for (String arg : args) {
			interpret(arg);
		}
	}
	
	public void run() throws Exception {
		
		CSVReader reader = new CSVReader(null);
		ConfigurationManager config = null;
		try {
			config = ConfigurationManager.getInstance();
		} catch (IOException e) {
			System.err.println("An error occurred while loading the configuration");
		}
		
		Extractor extractor = null;
		switch (this.runnerType) {
		case RUNNER_REVIEW:
			System.out.println("Running the review extractor");
			extractor = ExtractorFactory.getExtractor(reader.getAppList(), config, "reviews");
			break;
		case RUNNER_INFO:
			System.out.println("Running the app info extractor");
			extractor = ExtractorFactory.getExtractor(reader.getAppList(), config, "info");
			break;
		}
		extractor.extract();
	}

	public void interpret(String arg) {
		String[] parts = arg.split("\\=");

		if (parts.length != 2)
			throw new IllegalArgumentException("The passed parameter is not valid: " + arg);

		String property = parts[0];
		String value = parts[1];

		if (property.equalsIgnoreCase("extractor")) {
			if (value.equalsIgnoreCase("reviews")) {
				this.runnerType = RUNNER_REVIEW;
			} else if (value.equalsIgnoreCase("info")) {
				this.runnerType = RUNNER_INFO;
			} else
				throw new IllegalArgumentException(
						"Illegal run type '" + value);
		}

	}
}
