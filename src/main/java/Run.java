import crawler.Constants;
import crawler.Crawler;

import java.util.Date;

//Start of the program
public class Main {

    public  static void main(String[] args){
        Crawler crawler = new Crawler();

        crawler.startGooglePlayStoreCrawler();
    }
}
