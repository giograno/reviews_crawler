package crawler;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import beans.AppInfo;
import config.ConfigurationManager;
import io.IWriter;
import io.WriterFactory;
import utils.ConfigurationException;
import utils.Utils;
import utils.WebElements;

public class GoogleInfoCrawler extends Crawler {

	private ArrayList<String> appList;
	private String currentApp;
	private IWriter writer;

	public GoogleInfoCrawler(ArrayList<String> appList) {
		
		try {
			this.writer = WriterFactory.getWriter();
		} catch (ConfigurationException e) {
			System.err.println(e.getMessage());
		}
		
		this.appList = appList;
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, ConfigurationManager.getInstance().getPathForPhantomJSDriver());
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--webdriver-loglevel=NONE");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		this.driver = new PhantomJSDriver(caps);
	}

	public void run() {

		for (String appName : appList) {

			this.currentApp = appName;
			String appLink = WebElements.PLAY_STORE_BASE_LINK + appName + WebElements.REVIEWS_LANGUAGE;
			this.connectTo(appLink);
			if (this.isAppLinkNotValid()) {
				// try without language
				appLink = WebElements.PLAY_STORE_BASE_LINK + appName;
				this.connectTo(appLink);
			}
			AppInfo info = this.getInfo();
			if (info != null) {
				this.writer.writeline(info);
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

	private void connectTo(String appLink) {
		driver.manage().window().maximize();
		driver.navigate().to(appLink);
	}

	private AppInfo getInfo() {
		AppInfo appInfo = null;
		
		if (!isAppLinkNotValid()) {

			try {
				appInfo 		= new AppInfo();
				String upDate 	= "n.d";
				String version 	= "n.d";
				String category = "n.d";

				if (this.aLastUpdateExists())
					upDate = driver.findElement(By.xpath(WebElements.LAST_UPDATE)).getText();
				if (this.aCurrentVersionExists())
					version = driver.findElement(By.xpath(WebElements.CURRENT_VERSION)).getText();
				else if (this.aRequiredSoftwareExists())
					version = driver.findElement(By.xpath(WebElements.REQUIRE_SOFTWARE)).getText();
				category = driver.findElement(By.xpath(WebElements.CATEGORY)).getText();

				appInfo.setAppName(currentApp);
				appInfo.setCurrentVersion(version);
				appInfo.setCategory(category);
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
		return this.isElementPresent(By.xpath(WebElements.LAST_UPDATE));
	}

	private boolean aCurrentVersionExists() {
		return this.isElementPresent(By.xpath(WebElements.CURRENT_VERSION));
	}

	private boolean aRequiredSoftwareExists() {
		return this.isElementPresent(By.xpath(WebElements.REQUIRE_SOFTWARE));
	}

	private boolean isAppLinkNotValid() {
		return this.isElementPresent(By.id("error-section"));
	}
}
