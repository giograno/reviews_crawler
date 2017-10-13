package io;

import beans.Exportable;
import beans.Review;
import com.jamesmurty.utils.XMLBuilder;
import config.ConfigurationManager;
import utils.Utils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

public class SURFWriter implements IWriter {

    private XMLBuilder builder;
    private Properties properties;

    public SURFWriter() {
        try {
            builder = XMLBuilder.create("reviews");
            properties = new Properties();
            properties.put(OutputKeys.METHOD, "xml");
            properties.put(OutputKeys.INDENT, "yes");
            properties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
            properties.put("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (ParserConfigurationException ex) { ex.printStackTrace(); }
    }

    @Override
    public void writeline(Exportable exportable) {
        try {
            writeLine((Review) exportable);
        } catch (IOException e) {
            System.err.println("An error occurred while performing writing on output");
        }
    }

    public synchronized void writeLine(Review review) throws IOException {
        builder.element("review")
                .element("date")
                .t(Utils.getStringFromDate(review.getReviewDate())).up()
                .element("star_rating")
                .t(String.valueOf(review.getNumberOfStars())).up()
                .element("user")
                .t(review.getAuthor()).up()
                .element("app_version")
                .t("").up()
                .element("review_title")
                .t(review.getTitle()).up()
                .element("review_text")
                .t(review.getReviewText()).up();
    }

    public void finalize(String appName) {
        try {
            builder.a("app", appName);
            PrintWriter writer = new PrintWriter(new FileOutputStream("reviews.xml"));
            builder.toWriter(writer, properties);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public Date getLastDate(ConfigurationManager config, String appName) {
        return null;
    }
}
