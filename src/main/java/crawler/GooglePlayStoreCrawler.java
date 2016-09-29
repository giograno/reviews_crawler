/**
 * Get all the reviews of a app based on the given app name
 */
package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GooglePlayStoreCrawler {
    private static final int searchLimit = 3; //TODO: only testing purpose --> remove afterwards
    private int count = 0;
    private boolean dateOfLastCrawlIsReached = false;

    //TODO: move them to right place --> Constants or into method
    private static final String play_store_base_link = "https://play.google.com/store/apps/details?id=com.";
    private static final String next_reviews_button = "//button[@aria-label='See More' and @class='expand-button expand-next']";
    private static final String reviews_language = "en";

    private Date dateOfLastCrawl = null; // is given as parameter if already crawled before
    private String appName = null;
    private WebDriver driver = null;
    private WebDriverWait wait = null;

    public GooglePlayStoreCrawler(String appName, Date dateOfLastCrawl){
        this.appName = appName;

        if(dateOfLastCrawl == null){
            System.out.println("Crawl all existing reviews of "+ appName);
        }else{
            System.out.println("Crawling all new reviews of "+ appName + " since " + dateOfLastCrawl);
            this.dateOfLastCrawl = dateOfLastCrawl;
        }
    }

    public List<Review> getReviewsByAppName(){

        driver = connectWithDriverOfLink(this.appName);
        clickNextButton();
        scrollPage(0,-250);
        changeReviewSortOrderToNewest();
        moveHoveSoItShowsReviewDate();

        return getReviewsByDriver();
    }

    private WebDriver connectWithDriverOfLink(String appName){
        String appLink = play_store_base_link + appName + "&hl=" + reviews_language;

        WebDriver driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.navigate().to(appLink);

        return driver;
    }
    private void clickNextButton() {
        //Wait til nextbutton is clickable
        this.wait = new WebDriverWait(driver, 10);
        this.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(next_reviews_button)));

        WebElement nextButton = driver.findElements(By.xpath(next_reviews_button)).get(1);
        nextButton.click();
    }
    private void scrollPage(int xAxis, int yAxis){
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("window.scrollBy(" + xAxis + "," + yAxis + ")", "");
    }
    private void changeReviewSortOrderToNewest() {
        //Open sortOrder Dropdown menu
        this.wait = new WebDriverWait(driver, 10);
        WebElement sortOrderButton = driver.findElements(By.className("dropdown-menu")).get(0);
        this.wait.until(ExpectedConditions.elementToBeClickable(sortOrderButton));
        sortOrderButton.click();

        //Change order to 'Newest'
        this.wait = new WebDriverWait(driver, 10);
        WebElement newestOrderButton = driver.findElement(By.xpath("//button[contains(.,'Newest')]"));
        this.wait.until(ExpectedConditions.elementToBeClickable(newestOrderButton));
        newestOrderButton.click();
    }
    //Focuses the mousehover somewhere else, otherwise review date is hidden
    private void moveHoveSoItShowsReviewDate() {
        WebElement hoverElement = driver.findElement(By.className("score"));
        Actions builder = new Actions(driver);
        builder.moveToElement(hoverElement).perform();
    }



    private List<Review> getReviewsByDriver() {
        List<Review> collectedReviews = new ArrayList<Review>();

        while (nextButtonExists(driver) && !dateOfLastCrawlIsReached) {
            sleep(2500);
            List <WebElement> newReviewsAsWebElement = getAllReviewsOfCurrentPage();

            collectedReviews = reviewsSortedOutByDate(newReviewsAsWebElement, dateOfLastCrawl, collectedReviews);
            clickNextButton();
        }
        return collectedReviews;
    }

    private List<WebElement> getAllReviewsOfCurrentPage() {
        final String reviewClassName = "single-review";
        return driver.findElements(By.className(reviewClassName));
    }

    private List<Review> reviewsSortedOutByDate(List<WebElement> crawledReviews, Date dateOfLastCrawl, List<Review> sortedReviews) {


        DateFormat formatter = new SimpleDateFormat("MMMM dd,yyyy", Locale.ENGLISH);
        Date date = null;

        for (WebElement review : crawledReviews) {
            //sort out the empty strings
            if(!review.getText().equals("")) {
                String dateAsText = review.findElement(By.className("review-date")).getText();
                String reviewText = review.findElement(By.className("review-body")).getText();

                if(dateOfLastCrawl != null){
                    //Parse date into Dateformat
                    try {
                        date = formatter.parse(dateAsText);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //add to List if newer than lastCrawl
                    if(date.after(dateOfLastCrawl)) {
                        System.out.println(dateAsText + " is newer than lastCrawl ");
                        Review newReview = new Review(reviewText, date, 5);
                        sortedReviews.add(newReview);
                    }else{
                        dateOfLastCrawlIsReached = true;
                        return sortedReviews;
                    }

                }
            }
        }
        return sortedReviews;
    }

    private boolean nextButtonExists(WebDriver driver) {
        List<WebElement> arrowButton = driver.findElements(By.xpath(next_reviews_button));

        if(arrowButton.size() >= 1){
            return true;
        }else{
            return false;
        }
    }

    //TODO: change to 'presenceOfElementLocated', but not working
    private void sleep(int milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


