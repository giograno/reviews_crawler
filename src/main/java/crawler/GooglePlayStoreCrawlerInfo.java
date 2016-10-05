/**
 * Get all the reviews of a app based on the given app name
 */
package crawler;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import beans.AppInfo;

public class GooglePlayStoreCrawlerInfo {

	private static final String play_store_base_link = "https://play.google.com/store/apps/details?id=";

	private static final String current_version = "//div[contains(@itemprop, 'softwareVersion')]";

	private static final String last_update = "//div[contains(@itemprop, 'datePublished')]";

	private WebDriver driver = null;

	public GooglePlayStoreCrawlerInfo(String browserChoice, File webDriverFile) {
		if (browserChoice.equalsIgnoreCase("Chrome")) {
			System.setProperty("webdriver.chrome.driver", webDriverFile.getAbsolutePath());
			driver = new ChromeDriver();

		} else if (browserChoice.equalsIgnoreCase("Firefox")) {
			driver = new FirefoxDriver();
		}
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
		if (appLink.startsWith(play_store_base_link)) {
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

			driver.manage().window().setPosition(new Point(-2000, 0));
			driver.navigate().to(appLink);

		}

	}

	private AppInfo getInfo() throws Exception {

		Boolean errorPresent = driver.findElements(By.id("error-section")).size() > 0;
		AppInfo appInfo = null;
		if (!errorPresent) {
			appInfo = new AppInfo();
			String version = driver.findElement(By.xpath(current_version)).getText();
			String upDate = driver.findElement(By.xpath(last_update)).getText();
			appInfo.setCurrentVersion(version);
			appInfo.setLastUpdate(upDate);

		}
		return appInfo;

	}

}
