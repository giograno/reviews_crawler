package beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents a review for an app; it stores the text review, the review date
 * and the number of stars
 * @author giograno
 *
 */
public class Review implements Exportable {
	private String appName;
	private String reviewText;
	private Date reviewDate;
	private int numberOfStars;
	
	private DateFormat formatter = new SimpleDateFormat("MMMM dd,yyyy", Locale.ENGLISH);

	public Review(String appName, String reviewText, Date reviewDate, int numberOfStars) {
		this.appName        = appName;
		this.reviewText 	= reviewText;
		this.reviewDate 	= reviewDate;
		this.numberOfStars  = numberOfStars;
	}

	public String getReviewText() {
		return reviewText;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public int getNumberOfStars() {
		return numberOfStars;
	}

	@Override
	public List<String> getFieldsToExport() {
		List<String> fields = new ArrayList<String>();
		fields.add(this.appName);
		fields.add(this.reviewText);
		fields.add(formatter.format(this.reviewDate));
		fields.add(String.valueOf(this.numberOfStars));
		return fields;
	}
}
