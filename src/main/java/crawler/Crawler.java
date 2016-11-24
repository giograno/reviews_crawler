package crawler;

import java.util.concurrent.Callable;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public abstract class Crawler implements Callable<Integer> { 
	
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

