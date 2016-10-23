package crawler.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import beans.AppInfo;
import config.ConfigurationManager;
import crawler.Crawler;
import utils.Utils;
import utils.WebElements;

public class PlayStoreCrawlerInfo extends Crawler {

	private ArrayList<String> appList;
	private String browserChoice;
	private String driverPath;
	private ConfigurationManager config;
	private WebDriver driver;
	private String currentApp;

	public PlayStoreCrawlerInfo(ConfigurationManager config, ArrayList<String> appList) {
		this.appList = appList;
		this.config = config;
		this.browserChoice = this.config.getBrowserChoice();
		this.driverPath = this.config.getWebDriver().getAbsolutePath();

		if (browserChoice.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver", driverPath);
			driver = new ChromeDriver();
		} else if (browserChoice.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();
		} else if ((browserChoice.equalsIgnoreCase("")) || (this.driverPath == null)) {
			throw new RuntimeException("Unvalid browser choice or driver path");
		}
	}

	@Override
	public void run() {

		for (String appName : appList) {

			this.currentApp = appName;
			String appLink = WebElements.PLAY_STORE_BASE_LINK + appName + WebElements.REVIEWS_LANGUAGE;
			connectTo(appLink);
			if (this.isAppLinkNotValid()) {
				// trying to remove the language
				appLink = WebElements.PLAY_STORE_BASE_LINK + appName;
				connectTo(appLink);
			}
			AppInfo info = this.getInfo();
			if (info != null) {
				try {
					this.writeLine(info);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		this.closeDriver();
	}

	public AppInfo getAppInformation(String appLink) throws Exception {
		connectTo(appLink);
		AppInfo info = this.getInfo();
		return info;
	}

	public void closeDriver() {
		driver.quit();
	}

	/**
	 * TODO: remove pattern
	 * 
	 * @param appLink
	 */
	private void connectTo(String appLink) {
		if (appLink.startsWith(WebElements.PLAY_STORE_BASE_LINK)) {
			boolean found = false;
			Pattern pattern = Pattern.compile("hl=[a-zA-Z]{2}");
			Matcher matcher = pattern.matcher(appLink);
			while (matcher.find()) {
				found = true;
				String oldLang = matcher.group();
				appLink = appLink.replace(oldLang, "hl=en");
			}
			if (!found) {
				appLink = appLink + "&hl=en";
			}
			if (driver == null) {

			}

			driver.manage().window().maximize();
			driver.navigate().to(appLink);

		}

	}

	private AppInfo getInfo() {
		AppInfo appInfo = null;
		
		if (!isAppLinkNotValid()) {

			try {
				appInfo = new AppInfo();
				String upDate = "n.d";
				String version = "n.d";

				if (this.aLastUpdateExists())
					upDate = driver.findElement(By.xpath(WebElements.LAST_UPDATE)).getText();
				if (this.aCurrentVersionExists())
					version = driver.findElement(By.xpath(WebElements.CURRENT_VERSION)).getText();
				else if (this.aRequiredSoftwareExists())
					version = driver.findElement(By.xpath(WebElements.REQUIRE_SOFTWARE)).getText();

				appInfo.setAppName(currentApp);
				appInfo.setCurrentVersion(version);
				appInfo.setLastUpdate(Utils.convertReviewFormat(upDate));
				System.out.println("Mined app info for: " + this.currentApp);
			} catch (NoSuchElementException e) {
				System.err.println("An error occurred while fetching current version and last update");
				return null;
			}
			
		} else {
			System.out.println("Not found page for: " + this.currentApp);
		}
		return appInfo;
	}

	private boolean aLastUpdateExists() {
		return driver.findElements(By.xpath(WebElements.LAST_UPDATE)).size() > 0;
	}

	private boolean aCurrentVersionExists() {
		return driver.findElements(By.xpath(WebElements.CURRENT_VERSION)).size() > 0;
	}

	private boolean aRequiredSoftwareExists() {
		return driver.findElements(By.xpath(WebElements.REQUIRE_SOFTWARE)).size() > 0;
	}

	private boolean isAppLinkNotValid() {
		return driver.findElements(By.id("error-section")).size() > 0;
	}
}
