package utils;

import org.openqa.selenium.By;

/**
 * Utility class for all the <WebElement> to mine
 * @author grano
 *
 */
public class WebElements {
	
	public static final String PLAY_STORE_BASE_LINK		= "https://play.google.com/store/apps/details?id=";
	public static final String NEXT_REVIEWS_BUTTON 		= "//button[@aria-label='See More' and @class='expand-button expand-next']";
	public static final String END_REVIEWS_BUTTON 		= "//button[@aria-label='See More' and @class='expand-button expand-next' and @style='display: none;']";
    public static final String REVIEWS_LANGUAGE 		= "&hl=en";
	public static final String CURRENT_VERSION 			= "//div[contains(@itemprop, 'softwareVersion')]";
	public static final String REQUIRE_SOFTWARE 		= "//div[contains(@itemprop, 'operatingSystem')]";
	public static final String LAST_UPDATE 				= "//div[contains(@itemprop, 'datePublished')]";	
	public static final String STARS					= ".//div[@class='tiny-star star-rating-non-editable-container']";
	public static final String SECTION_REVIEW      		= "//div[@class='details-section reviews']";
	public static final String CATEGORY					= "//span[@itemprop='genre']";
	public static final String NUM_DOWNLOADS 			= "//div[contains(@itemprop, 'numDownloads')]";

	/**
	 * Returns the <By> object for the chosen reviews order
	 * @param choice
	 * @return
	 */
	public static By getOrderButton(String choice) {
		if (choice.equals("newest"))
			return By.xpath("//button[contains(.,'Newest')]");
		else if (choice.equals("helpfulness"))
			return By.xpath("//button[contains(.,'Helpfulness')]");
		else 
			return null;
	}
}
