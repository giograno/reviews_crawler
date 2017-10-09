import config.ConfigurationManager;
import extractors.Extractor;
import extractors.ExtractorFactory;
import importer.FileImporter;
import io.AppListReader;
import io.CSVReader;
import io.FixedInput;
import io.TxtReader;

@SuppressWarnings("deprecation")
public class Run {

	private static final int RUNNER_REVIEW 		= 0;
	private static final int RUNNER_INFO 		= 1;
	private static final int RUNNER_EXPORTER 	= 2;
	private static final int RUNNER_IMPORTER 	= 3;

	private int runnerType;

	private String app;

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

		if (app!=null)
		    reader = new FixedInput(null, app);
		else if (config.getInputCsv().endsWith(".csv"))
			reader = new CSVReader(null);
		else if (config.getInputCsv().endsWith(".txt")) 
			reader = new TxtReader(null);
		else 
			throw new RuntimeException("Invalid format for input file");
		
		Extractor extractor;
		switch (this.runnerType) {
		case RUNNER_REVIEW:
			System.out.println("Running the review extractor");
			extractor = ExtractorFactory.getExtractor(reader.getAppList(), config, "reviews");
			extractor.printNumberOfInputApps();
			extractor.extract();
			break;
		case RUNNER_INFO:
			System.out.println("Running the app info extractor");
			extractor = ExtractorFactory.getExtractor(reader.getAppList(), config, "info");
			extractor.printNumberOfInputApps();
			extractor.extract();
			break;
		case RUNNER_EXPORTER:
			System.out.println("Exporting the reviews");
			extractor = ExtractorFactory.getExtractor(reader.getAppList(), config, "export");
			extractor.printNumberOfInputApps();
			extractor.extract();
			break;
		case RUNNER_IMPORTER:
			System.out.println("Importing reviews from file");
			FileImporter importer = new FileImporter(config);
			importer.importReviews();
			break;
		}
	}

	public void interpret(String arg) {
		String[] parts = arg.split("\\=");

		if (parts.length != 2)
			throw new IllegalArgumentException("The passed parameter is not valid: " + arg);

		String property = parts[0];
		String value = parts[1];

        if (property.equalsIgnoreCase("app")) {
		    if (!value.isEmpty())
		        app = value;
        }

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
		} else if (property.equalsIgnoreCase("import")) {
			if (value.equalsIgnoreCase("reviews")) {
				this.runnerType = RUNNER_IMPORTER;
			} else
				throw new IllegalArgumentException(
						"Illegal run type '" + value);
		}
 	}
}
