package crawler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import beans.Review;
import io.CSVWriter;
import utils.Utils;
import utils.WebElements;

public class PlayStoreCrawler extends Crawler {

	private boolean dateOfLastCrawlIsReached = false;

	private Date dateOfLastCrawl;
	private String appName = null;
	private final String gecko = "/Users/giograno/Documents/Zurich_PhD/work_zurich/reviewCrawler/geckodriver";
	
	private WebDriver driver;
	private WebDriverWait wait;
	
	private List<Review> reviews;

	public PlayStoreCrawler(String appName, Date dateOfLastCrawl) {
		this.appName = appName;
		this.reviews = new ArrayList<>();

		if (dateOfLastCrawl == null) {
//			System.out.println("Crawl all existing reviews of " + appName);
			this.dateOfLastCrawl = Utils.getFakeOldDate();
		} else {
//			System.out.println("Crawling all new reviews of " + appName + " since " + dateOfLastCrawl);
			this.dateOfLastCrawl = dateOfLastCrawl;
		}
	}

	@Override
	public void run() {
		System.out.println("*------------------------------------------------------*");
		System.out.println("Running on app: " + this.appName);
		connectWithDriverOfLink(this.appName);
		if (this.areReviewsExpandable())
			clickNextButton();
		else {
			System.out.println("It is no possibile to see the reviews for: " + this.appName);
			this.driver.close();
			return;
		}
		scrollPage(0, -250);
		changeReviewSortOrderToNewest();
		moveHoveSoItShowsReviewDate();

		getReviewsByDriver();

		for (Review review : reviews) {
			try {
				CSVWriter.writeline(review.getFieldsToExport());
			} catch (IOException e) {
				System.err.println("An error occurred during the export of a review");
			}
		}
		if (reviews.size() > 0) 
			CSVWriter.writeSuccess(this.appName);
		System.out.println("Writed " + reviews.size() + " for: " + this.appName);
		this.driver.close();
	}

	public List<Review> getReviews() {
		return this.reviews;
	}

	private void connectWithDriverOfLink(String appName) {
		String appLink = WebElements.PLAY_STORE_BASE_LINK + appName + WebElements.REVIEWS_LANGUAGE;
		
		// initialize the driver
//		this.driver = new FirefoxDriver();
		System.setProperty("webdriver.gecko.driver", this.gecko);
		DesiredCapabilities capabilities=DesiredCapabilities.firefox();
	    capabilities.setCapability("marionette", true);
		this.driver = new FirefoxDriver(capabilities);
		this.wait = new WebDriverWait(this.driver, 10);

		driver.manage().window().maximize();
		driver.navigate().to(appLink);
	}
	
	private void clickNextButton() {
		// wait until next button could be clicked
		this.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(WebElements.NEXT_REVIEWS_BUTTON)));
	

		WebElement nextButton = driver.findElements(By.xpath(WebElements.NEXT_REVIEWS_BUTTON)).get(1);
		nextButton.click();
	}

	private void scrollPage(int xAxis, int yAxis) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(" + xAxis + "," + yAxis + ")", "");
	}

	private void changeReviewSortOrderToNewest() {
		// open sort order drop down menu
		WebElement sortOrderButton = driver.findElements(By.className("dropdown-menu")).get(0);
		this.wait.until(ExpectedConditions.elementToBeClickable(sortOrderButton));
		sortOrderButton.click();

		// change order to 'Newest'
		WebElement newestOrderButton = driver.findElement(By.xpath("//button[contains(.,'Newest')]"));
		this.wait.until(ExpectedConditions.elementToBeClickable(newestOrderButton));
		newestOrderButton.click();
	}

	// Focuses the mousehover somewhere else, otherwise review date is hidden
	private void moveHoveSoItShowsReviewDate() {
		WebElement hoverElement = driver.findElement(By.className("score"));
		Actions builder = new Actions(driver);
		builder.moveToElement(hoverElement).perform();
	}

	//TODO sleep to avoid stale element (fix it)
	private void getReviewsByDriver() {

		boolean isTheLastPage = false;

		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (!isTheLastPage && !dateOfLastCrawlIsReached) {
			List<WebElement> newReviewsAsWebElement = getAllReviewsOfCurrentPage();

			this.getReviewsOfThePage(newReviewsAsWebElement, dateOfLastCrawl);

			if (nextButtonExists())
				clickNextButton();
			if (this.isTheLastPage())
				isTheLastPage = true;
		}
	}

	private List<WebElement> getAllReviewsOfCurrentPage() {
		final String reviewClassName = "single-review";
		return this.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(reviewClassName)));
//		return this.driver.findElements(By.className(reviewClassName));
	}

	private void getReviewsOfThePage(List<WebElement> crawledReviews, Date dateOfLastCrawl) {

		DateFormat formatter = new SimpleDateFormat("MMMM dd,yyyy", Locale.ENGLISH);
		Date date = null;
		
		for (WebElement review : crawledReviews) {
			// sort out the empty strings
			if (!review.getText().equals("")) {
				String dateAsText = review.findElement(By.className("review-date")).getText();
				String reviewText = review.findElement(By.className("review-body")).getText();
				// review.findElement(By.)

				if (dateOfLastCrawl != null) {
					try {
						date = formatter.parse(dateAsText);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					// add to List if newer than lastCrawl
					if (date.after(dateOfLastCrawl)) {
						Review newReview = new Review(this.appName, reviewText, date, this.getNumberOfStars(review));
						this.reviews.add(newReview);
					} else
						dateOfLastCrawlIsReached = true;
				}
			}
		}
	}

	private boolean nextButtonExists() {
		List<WebElement> arrowButton = this.driver.findElements(By.xpath(WebElements.NEXT_REVIEWS_BUTTON));
		List<WebElement> hiddenButton = this.driver.findElements(By.xpath(WebElements.END_REVIEWS_BUTTON));

		if (arrowButton.size() >= 1 && hiddenButton.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isTheLastPage() {
		List<WebElement> hiddenButton = this.driver.findElements(By.xpath(WebElements.END_REVIEWS_BUTTON));
		if (hiddenButton.size() > 0)
			return true;
		else
			return false;
	}

	private boolean areReviewsExpandable() {
		WebElement sectionReview;

		if (this.driver.findElements(By.xpath(WebElements.SECTION_REVIEW)).size() >= 1) {
			sectionReview = this.driver.findElement(By.xpath(WebElements.SECTION_REVIEW));
			List<WebElement> arrowButton = sectionReview.findElements(By.xpath(WebElements.NEXT_REVIEWS_BUTTON));
			if (arrowButton.size() > 1)
				return true;
			else
				return false;
		} else
			return false;
	}

	private int getNumberOfStars(WebElement review) {
		String stars = review.findElement(By.xpath(WebElements.STARS)).getAttribute("aria-label");

		for (int i = 1; i < 6; i++) {
			if (stars.contains(Integer.toString(i)))
				return i;
		}
		return 0;
	}
	
}
