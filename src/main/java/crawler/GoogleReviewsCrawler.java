package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.SURFWriter;
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

/**
 * The crawler for the Google PlayStore
 * @author grano
 *
 */
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

		sleep(2000);
		if (this.areReviewsExpandable())
			clickNextButton();
		else {
			System.out.println("It is no possible to see the reviews for: " + this.appName);
			if (this.writer instanceof CSVWriter)
				((CSVWriter) this.writer).writeSuccess(this.appName);
			this.driver.quit();
			return;
		}
		scrollPage(0, -250);
		changeReviewsOrder();
		moveHoveSoItShowsReviewDate();

		getReviewsByDriver();

		for (Review review : reviews) {
			this.writer.writeline(review);
		}
		
		if (this.writer instanceof CSVWriter)
            ((CSVWriter) this.writer).writeSuccess(this.appName);
        if (this.writer instanceof SURFWriter)
            ((SURFWriter) writer).finalize(appName);
		System.out.println(
				"Extraction completed for: " + this.appName + "\nTotal reviews extracted = " + this.reviews.size());
		this.driver.quit();
	}

	private void setDriverAndConnect(String appName) {
		// instantiate the writer
		try {
			this.writer = WriterFactory.getWriter();
		} catch (ConfigurationException e) {
			System.err.println(e.getMessage());
		}
		
		this.reviews 		= new ArrayList<>();
		this.reviewsCounter = 0;
		// get the date of the last reviews crawler (for mongoDB) 
		// or the last date to consider in the crawling process for the csv extraction
		this.endingDate		= this.writer.getLastDate(this.configuration, this.appName);
		if (this.endingDate == null)
			this.endingDate = Utils.getFakeOldDate();
		System.out.println("Mining from last review date = " + this.endingDate);
		this.limit 			= configuration.getLimit();
		
		System.out.println("Crawling from " + this.endingDate);

		String appLink = WebElements.PLAY_STORE_BASE_LINK + appName + WebElements.REVIEWS_LANGUAGE;

		this.instanceDriver();
		
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
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
				if (indexToStart == maximumReviews) {
					isTheLastPage = true;
					continue;
				}
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
				String author = review.findElement(By.className("author-name")).getText();
				String title = review.findElement(By.className("review-title")).getText();
				
				// go to the next review if this one is not ok
				if (!isAValidReview(reviewText))
					continue;

				if (this.endingDate != null) {
					date = Utils.getExtendedDateFromString(dateAsText.replaceAll(",", ""));
					if (date.after(endingDate)) {
						// continue if before the starting date
						if (this.startingDate != null && date.before(this.startingDate)) 
							continue;
						String id = Utils.getTimeBasedUUID();

						// todo: To improve
						Review newReview;
						if (configuration.getFormat().equalsIgnoreCase("surf"))
                            newReview = new Review(id, this.appName, reviewText, date, this.getNumberOfStars(review), author, title);
						else
						    newReview = new Review(id, this.appName, reviewText, date, this.getNumberOfStars(review));
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
	 * Checks if a stings is valid as a review; at least one valid alphabet letter
	 * should be in the text
	 * @param reviewText	the review text
	 * @return				true or false
	 */
	private boolean isAValidReview(String reviewText) {
		if (reviewText.trim().equals(""))
			return false;
		if (reviewText.matches(".*[a-zA-Z]+.*"))
			return true;
		return false;
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
