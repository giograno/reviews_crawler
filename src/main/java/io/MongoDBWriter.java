package io;

import java.util.Date;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

import beans.Exportable;
import beans.Review;

public class MongoDBWriter implements IWriter {

	private Morphia morphia;
	private Datastore datastore;

	public MongoDBWriter() {
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
	
	public Date lastReviewForApp(String appName) {
		return null;
	}
}
