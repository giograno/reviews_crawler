package crawler;

import java.util.List;

import beans.Review;

public interface Crawler {

    public void startCrawling();

	public List<Review> getReviews();
    
}
