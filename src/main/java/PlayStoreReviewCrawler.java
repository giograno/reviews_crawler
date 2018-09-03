import config.ConfigurationManager;

import java.util.Arrays;

public class PlayStoreReviewCrawler {

    public static void main(String[] args) {

    }

    public PlayStoreReviewCrawler(String[] args) {
        Arrays.stream(args).forEach(this::interpret);
    }

    public void interpret(String arg) {
        ConfigurationManager config = ConfigurationManager.getInstance();
    }
}
