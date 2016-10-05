package crawler;

import java.util.Date;

public class Review {
	private String reviewText;
	private Date reviewDate;
	private int numberOfStars;

	public Review(String reviewText, Date reviewDate, int numberOfStars) {
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
}
