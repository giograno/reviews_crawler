package config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import utils.Utils;

/**
 * Handles the tool configuration
 * @author giograno
 *
 */
public class ConfigurationManager {
	
	private static final String STATIC_CONTENT = "---------STATIC+CONTENT";
	private static ConfigurationManager instance;
	private static String filename;
	private static String content;
	
	private String myFilename;
	
	private Properties properties;
	
	static {
		setFilename("config.properties");
	}
	
	/**
	 * Sets the filename of the configuration file. To be called before <code>getInstance</code>.
	 * @return
	 */
	public static void setFilename(String pFilename) {
		filename = pFilename;
		content = null;
	}
	
	public static void setDirectContent(String pContent) {
		content = pContent;
		filename = STATIC_CONTENT + UUID.randomUUID();
	}
	
	/**
	 * Returns an instance of the configuration manager. Call <code>setFilename</code> before this method.
	 * @return	the instance of <ConfigurationManager>
	 */
	public static ConfigurationManager getInstance() {
		
		try {
		if (instance == null || !instance.myFilename.equals(filename)) {
			if (!filename.startsWith(STATIC_CONTENT))
				instance = new ConfigurationManager(filename);
			else {
				instance = new ConfigurationManager();
				instance.directLoadContent(filename, content);
			}
		}
		} catch (IOException exception) {
			System.err.println("A problem occurred with the Configuration Manager");
		}
		return instance;
	}
	
	private ConfigurationManager(String filename) throws IOException {
		this.properties = new Properties();
		this.properties.load(new FileInputStream(filename));
		
		this.myFilename = filename;
	}
	
	public ConfigurationManager() {
		this.myFilename = "";
	}
	
	public void directLoadContent(String pFilename, String pContent) throws IOException {
		this.properties = new Properties();
		this.properties.load(new StringReader(pContent));
		
		this.myFilename = pFilename;
	}
	
	/**
	 * Returns the store to mine
	 * @return	 the store
	 */
	public String getStoreToCrawl() {
		return this.properties.getProperty("store", "google");
	}
	
	/**
	 * Returns the filename of the input CSV file
	 * @return	the name of the input file
	 */
	public String getInputCsv() {
		return this.properties.getProperty("input_file", "");
	}
	
	/**
	 * Returns the filename of the output CSV file
	 * @return	the name of the output file
	 */
	public String getOutputCsv() {
		return this.properties.getProperty("output_file", "output.csv");
	}
		
	/**
	 * Returns the starting date for the review crawling
	 * @return	the starting date or a null value if not specified
	 */
	public Date getStartingDate() {
		String auxDate = this.properties.getProperty("from", "");
		if (auxDate.equals(""))
			return null;
		else 
			return Utils.getDateFromString(auxDate);
	}
	
	/**
	 * Returns the ending date for the review crawling
	 * @return	the ending date or a null value if not specified
	 */
	public Date getEndDate() {
		String auxDate = this.properties.getProperty("to", "");
		if (auxDate.equals(""))
			return null;
		else 
			return Utils.getDateFromString(auxDate);
	}
	
	/**
	 * Returns the maximum of reviews to be extracted for a given app
	 * @return	a value of 1000 if not specified
	 */
	public int getLimit() {
		String aux = this.properties.getProperty("limit", "1000");
		return Integer.parseInt(aux);
	}
	
	/**
	 * Updates the date of last crawl in the 
	 * @throws Exception 
	 */
	public void updateDateOfLastCrawl() throws Exception {
		this.properties.setProperty("lastCrawl", Utils.getCurrentDate());
		this.properties.store(new FileOutputStream(filename), null);
	}
	
	/**
	 * Returns the number of thread used by the tool
	 * @return	the number of threads to use
	 */
	public int getNumberOfThreadToUse() {
		return Integer.parseInt(this.properties.getProperty("thread", "2"))	;
	}
	
	/**
	 * Returns the path for the PhantomJS Driver
	 * @return	the path of the driver
	 */
	public String getPathForPhantomJSDriver() {
		return this.properties.getProperty("phantomJS_path", "phantomjs");
	}
	
	/**
	 * Returns the chosen reviews order
	 * @return	the order
	 */
	public String getReviewsOrder() {
		return this.properties.getProperty("get_reviews_for", "newest");
	}
	
	/**
	 * Returns the chosen kind of file saving (mongodb or file)
	 * @return	the choice
	 */
	public String getHowToStore() {
		return this.properties.getProperty("export_to", "file");
	}

	/**
	 * Return the format in which the reviews are saved (plain csv or SURF format)
	 * @return
	 */
	public String getFormat() {
		return this.properties.getProperty("format","csv");
	}
}
