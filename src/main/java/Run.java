import config.ConfigurationManager;
import extractors.Extractor;
import extractors.ExtractorFactory;
import io.AppListReader;
import io.CSVReader;
import io.TxtReader;

@SuppressWarnings("deprecation")
public class Run {

	private static final int RUNNER_REVIEW = 0;
	private static final int RUNNER_INFO = 1;
	private static final int RUNNER_EXPORTER = 2;

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
		
		AppListReader reader;
		ConfigurationManager config = ConfigurationManager.getInstance();
		
		if (config.getInputCsv().endsWith(".csv"))
			reader = new CSVReader(null);
		else if (config.getInputCsv().endsWith(".txt")) 
			reader = new TxtReader(null);
		else 
			throw new RuntimeException("Invalid format for input file");
		
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
		case RUNNER_EXPORTER:
			System.out.println("Exporting the reviews");
			extractor = ExtractorFactory.getExtractor(reader.getAppList(), config, "export");
			break;
		}
		extractor.printNumberOfInputApps();
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
			} else if (value.equalsIgnoreCase("export")) {
				this.runnerType = RUNNER_EXPORTER;
			} else
				throw new IllegalArgumentException(
						"Illegal run type '" + value);
		}
	}
}
