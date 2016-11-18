package io;

import java.util.Date;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

import beans.Exportable;
import beans.Review;

/**
 * Handles the connections with the mongodb database
 * @author grano
 *
 */
public class MongoDBHandler implements IWriter {

	private Morphia morphia;
	private Datastore datastore;

	public MongoDBHandler() {
		this.morphia = new Morphia();
		morphia.map(Review.class);
		this.datastore = morphia.createDatastore(new MongoClient(),	"reviews");		
		datastore.ensureIndexes();
	}
	
	@Override
	public void writeline(Exportable exportable) {
		this.datastore.save(exportable);
	}

	public List<Review> getReviewsFromDB(String appName) {
		Query<Review> query = this.datastore.createQuery(Review.class)
				.field("appName").equal(appName);
		return query.asList();
	}
	
	/**
	 * Returns the date of the last review written in the database
	 * @param appName	the name of the app
	 * @return			the <Date> of the last review for the given app
	 */
	public Date getLastReviewForApp(String appName) {
		Review last = this.datastore.find(Review.class)
				.order("-reviewDate").get();
		return last.getReviewDate();
	}
}
