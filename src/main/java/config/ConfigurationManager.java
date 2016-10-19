package config;

import java.io.File;
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
	 * @return Instance of the manager
	 * @throws IOException If there is an error opening the file
	 */
	public static ConfigurationManager getInstance() throws IOException {
		if (instance == null || !instance.myFilename.equals(filename)) {
			if (!filename.startsWith(STATIC_CONTENT))
				instance = new ConfigurationManager(filename);
			else {
				instance = new ConfigurationManager();
				instance.directLoadContent(filename, content);
			}
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
	 * @return
	 */
	public String getStoreToCrawl() {
		return this.properties.getProperty("store", "google");
	}
	
	/**
	 * Returns the filename of the input CSV file
	 * @return
	 */
	public String getInputCsv() {
		return this.properties.getProperty("input.csv", "");
	}
	
	/**
	 * Returns the filename of the output CSV file
	 * @return
	 */
	public String getOutputCsv() {
		return this.properties.getProperty("reviews.output.csv", "reviews.csv");
	}
	
	/**
	 * Returns the date of last crawl
	 * @return
	 */
	public Date getDateOfLastCrawl() {
		String auxDate = this.properties.getProperty("lastCrawl", "");
		if (auxDate.equals(""))
			return null;
		else 
			return Utils.getDateFromString(auxDate);
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
	 * @return
	 */
	public int getNumberOfThreadToUse() {
		return Integer.parseInt(this.properties.getProperty("thread", "1"))	;
	}
	
	/**
	 * Returns the <code>File</code> for the web driver used for Chrome
	 * @return
	 */
	public File getWebDriver() {
		return new File(this.properties.getProperty("webdriver"));
	}
	
	/**
	 * Returns the browser choice 
	 * @return
	 */
	public String getBrowserChoice() {
		return this.properties.getProperty("browser", "firefox");
	}
	
}
