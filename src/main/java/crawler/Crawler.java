package crawler;


import java.util.Date;
import java.util.List;



public class Crawler implements Constants {

        Date testDate = new Date(116, 02, 23);

        public void startGooglePlayStoreCrawler(){

            System.out.println(testDate);

            for(final String appName : APP_NAMES){
                new Thread(new Runnable() {
                    public void run() {
                        GooglePlayStoreCrawler googlePlayStoreCrawler = new GooglePlayStoreCrawler(appName, testDate);

                        List<Review> googleReviews = googlePlayStoreCrawler.getReviewsByAppName();



                        //TODO: remove. Print out for testing
                        for (Review review: googleReviews){
                            System.out.println(review.getReviewText() + "// Date: " + review.getReviewDate().toString() + " // Stars: " + review.getNumberOfStars());
                        }
                    }
                }).start();
            }
        }

    //TODO: insert class Apple-Crawler here
}
