package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import beans.Review;
import config.ConfigurationManager;
import io.CSVWriter;
import io.IWriter;
import io.WriterFactory;
import utils.ConfigurationException;
import utils.Utils;
import utils.WebElements;

public class GoogleReviewsCrawler extends Crawler {

	private boolean endingDateReached = false;
	private ConfigurationManager configuration;
	private Date startingDate;
	private Date endingDate;
	private int limit;
	private String appName = null;
	private WebDriverWait wait;
	private List<Review> reviews;
	private int reviewsCounter;
	private IWriter writer;
	
	public GoogleReviewsCrawler(String appName, ConfigurationManager configuration) {
		this.appName 		= appName;
		this.configuration 	= configuration;
	}

	@Override
	public void run() {
		System.out.println("Extraction started for = " + this.appName);

		this.setDriverAndConnect(this.appName);

		if (this.areReviewsExpandable())
			clickNextButton();
		else {
			System.out.println("It is no possibile to see the reviews for: " + this.appName);
			if (this.writer instanceof CSVWriter)
				((CSVWriter) this.writer).writeSuccess(this.appName);
			this.driver.close();
			return;
		}
		scrollPage(0, -250);
		changeReviewsOrder();
		moveHoveSoItShowsReviewDate();

		getReviewsByDriver();

		for (Review review : reviews) {
			this.writer.writeline(review);
		}
		
		if (reviews.size() > 0) 
			if (this.writer instanceof CSVWriter)
				((CSVWriter) this.writer).writeSuccess(this.appName);
		System.out.println(
				"Extraction completed for: " + this.appName + "\nTotal reviews extracted = " + this.reviews.size());
		this.driver.quit();
	}

	private void setDriverAndConnect(String appName) {
		this.reviews 		= new ArrayList<>();
		this.reviewsCounter = 0;
		this.startingDate 	= configuration.getStartingDate() == null ? 
				null : configuration.getStartingDate();
		this.endingDate 	= configuration.getEndDate() == null ? 
				Utils.getFakeOldDate() : configuration.getEndDate();
		this.limit 			= configuration.getLimit();

		String appLink = WebElements.PLAY_STORE_BASE_LINK + appName + WebElements.REVIEWS_LANGUAGE;

		this.instanceDriver();

		driver.manage().window().maximize();
		driver.navigate().to(appLink);
	}
	
	/**
	 * Waits until next button is clickable and clicks it
	 */
	private void clickNextButton() {
		this.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(WebElements.NEXT_REVIEWS_BUTTON)));
		WebElement nextButton = driver.findElements(By.xpath(WebElements.NEXT_REVIEWS_BUTTON)).get(1);
		nextButton.click();
	}

	private void scrollPage(int xAxis, int yAxis) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(" + xAxis + "," + yAxis + ")", "");
	}

	private void changeReviewsOrder() {
		// change the reviews order
		WebElement sortOrderButton = driver.findElements(By.className("dropdown-menu")).get(0);
		this.wait.until(ExpectedConditions.elementToBeClickable(sortOrderButton));
		sortOrderButton.click();

		sleep(1000);
		
		WebElement orderButton = driver.findElement(WebElements.getOrderButton(ConfigurationManager.getInstance().getReviewsOrder()));
		this.wait.until(ExpectedConditions.elementToBeClickable(orderButton));
		orderButton.click();
	}

	/**
	 * Focuses the mouse somewhere else
	 */
	private void moveHoveSoItShowsReviewDate() {
		WebElement hoverElement = driver.findElement(By.className("score"));
		Actions builder = new Actions(driver);
		builder.moveToElement(hoverElement).perform();
	}

	private void getReviewsByDriver() {

		try {
			this.writer = WriterFactory.getWriter();
		} catch (ConfigurationException e) {
			System.err.println(e.getMessage());
		}
		boolean isTheLastPage = false;

		sleep(2000);

		int indexToStart = 0;
		int maximumReviews = 0;
		boolean mine = true;

		List<WebElement> newReviewsAsWebElement = null;

		while (!isTheLastPage && !endingDateReached) {

			if (mine) {
				newReviewsAsWebElement = getAllReviewsOfCurrentPage();
				maximumReviews = newReviewsAsWebElement.size();
			}

			indexToStart = this.getReviewsOfThePage(newReviewsAsWebElement, indexToStart,
					maximumReviews);

			if (indexToStart == maximumReviews)
				mine = true;
			else
				mine = false;

			if (mine)
				sleep(2000);

			if (this.isTheLastPage()) {
				isTheLastPage = true;
				continue;
			} else
				clickNextButton();
		}

	}

	private List<WebElement> getAllReviewsOfCurrentPage() {
		final String reviewClassName = "single-review";
		return this.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(reviewClassName)));
	}

	private int getReviewsOfThePage(List<WebElement> crawledReviews, int indexToStart, int maximumReviews) {
		Date date = null;

		sleep(250);

		for (; indexToStart < maximumReviews; indexToStart++) {
			WebElement review = crawledReviews.get(indexToStart);

			if (!review.getText().equals("")) {

				String dateAsText = review.findElement(By.className("review-date")).getText();
				String reviewText = review.findElement(By.className("review-body")).getText();

				if (this.endingDate != null) {
					date = Utils.getExtendedDateFromString(dateAsText);
					if (date.after(endingDate)) {
						// continue if before the starting date
						if (this.startingDate != null && date.before(this.startingDate)) 
							continue;
						Review newReview = new Review(this.appName, reviewText, date, this.getNumberOfStars(review));
						this.reviews.add(newReview);
						
						this.reviewsCounter++;
						if ((this.reviewsCounter % 50) == 0)
							System.out.println(
									"Mined " + this.reviewsCounter + " for " + this.appName + " - Date = " + date);

						// limits the crawling to a fixed amount of reviews
						if (this.reviewsCounter >= this.limit)
							endingDateReached = true;
					} else
						endingDateReached = true;
				}
			} else
				break;
		}

		return indexToStart;
	}

	/**
	 * Looks for the next button which become invisible
	 * 
	 * @return true or false
	 */
	private boolean isTheLastPage() {
		return this.isElementPresent(By.xpath(WebElements.END_REVIEWS_BUTTON));
	}

	/**
	 * Looks for the next reviews button in the section review
	 * @return	true or false
	 */
	private boolean areReviewsExpandable() {
		if (this.isElementPresent(By.xpath(WebElements.SECTION_REVIEW))) {
			List<WebElement> arrowButton = this.driver.findElement(By.xpath(WebElements.SECTION_REVIEW)).
					findElements(By.xpath(WebElements.NEXT_REVIEWS_BUTTON));
			if (arrowButton.size() > 1)
				return true;
			else
				return false;
		} else
			return false;
	}

	/**
	 * Returns the number of stars of the app
	 * 
	 * @param review
	 *            the <WebElement> which contains the review
	 * @return the number of stars
	 */
	private int getNumberOfStars(WebElement review) {
		String stars = review.findElement(By.xpath(WebElements.STARS)).getAttribute("aria-label");
		stars = stars.replaceAll("[^0-9]+", " ");
		return Integer.parseInt(Arrays.asList(stars.trim().split(" ")).get(0));
	}

	/**
	 * Sets properly the driver
	 */
	private void instanceDriver() {
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, ConfigurationManager.getInstance().getPathForPhantomJSDriver());
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--webdriver-loglevel=NONE");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		this.driver = new PhantomJSDriver(caps);

		this.driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		this.wait = new WebDriverWait(this.driver, 30);
	}

	/**
	 * Sleeps the thread
	 * 
	 * @param milliseconds
	 *            duration of the sleep
	 */
	private void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
