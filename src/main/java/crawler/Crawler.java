package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

/**
 * Interface of a crawler. Implement <code>Runnable</code>
 * Each crawler for a different store or platform must implement this interface
 * @author grano
 *
 */
public abstract class Crawler implements Runnable { 

	protected WebDriver driver;
	
	/**
	 * Checks if a element is present
	 * 
	 * @param by
	 *            the <WebElement> to check
	 * @return true or false
	 */
	public synchronized boolean isElementPresent(By by) {
		try {
			this.driver.findElement(by);
			return true;
		} catch (NoSuchElementException exception) {
			return false;
		}
	}
}

