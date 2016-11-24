package beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import utils.Utils;

/**
 * Represents a review for an app; it stores the text review, the review date
 * and the number of stars
 * 
 * @author giograno
 *
 */
@Entity
public class Review implements Exportable {
	
	@Id
	private String id;
	@Property
	private String appName;
	@Property
	private String reviewText;
	@Property
	private Date reviewDate;
	@Property
	private int numberOfStars;

	public Review(String id, String appName, String reviewText, Date reviewDate, int numberOfStars) {
		this.id 			= id;
		this.appName 		= appName;
		this.reviewText 	= reviewText;
		this.reviewDate 	= reviewDate;
		this.numberOfStars 	= numberOfStars;
	}
	
	public Review(String[] reviewLine) {
		this.id 			= reviewLine[0];
		this.appName 		= reviewLine[1];
		this.reviewText 	= reviewLine[2];
		this.reviewDate 	= Utils.getExtendedDateFromString(reviewLine[3]);
		this.numberOfStars 	= Integer.parseInt(reviewLine[4]);
	}
	
	public Review(){ }
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNumberOfStars(int numberOfStars) {
		this.numberOfStars = numberOfStars;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
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
		fields.add(this.id);
		fields.add(this.appName);
		fields.add(this.reviewText);
		fields.add(Utils.getStringFromDate(this.reviewDate));
		fields.add(String.valueOf(this.numberOfStars));
		return fields;
	}

	@Override
	public String toString() {
		return this.appName + "=" + this.reviewText;
	}

}