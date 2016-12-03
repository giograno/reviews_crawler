package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Provides some utilities like dates conversion
 * @author grano
 *
 */
public class Utils {
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat lastUpdateFormatter = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);

	private static TimeBasedGenerator TB_GENERATOR = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
	
	/**
	 * Formats a <code>String</code> into a <code>Date</code>
	 * @param dateToParse in format dd/MM/yyyy
	 * @return a <code>Date</code>
	 */
	public synchronized static Date getDateFromString(String dateToParse) {
		Date date = null;
		
        try {
            date = formatter.parse(dateToParse);
        } catch (ParseException e) {
        	System.err.println("WTF there is a problem in date conversion;"
        			+ "\nTrying to convert " + dateToParse);
        	e.printStackTrace();
        }
        
        return date;
	}
	
	/**
	 * Formats a <code>String</code> into a <code>Date</code>
	 * @param dateToParse
	 * @return
	 */
	public synchronized static Date getExtendedDateFromString(String dateToParse) {
		Date date = null;
		
        try {
            date = lastUpdateFormatter.parse(dateToParse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return date;
	}
	
	public synchronized static String convertReviewFormat(String reviewDate) {
		String aux = null;
		try {
			aux =formatter.format(lastUpdateFormatter.parse(reviewDate));
		} catch (ParseException e) {
			System.err.println("An error occurred while parsing the date");
		}
		return aux;
	}
	
	/**
	 * Converts a <Date> into a <String> object using the format MMMM dd, yyyy
	 * @param date	the date to convert
	 * @return		the date as a string
	 */
	public synchronized static String getStringFromDate(Date date) {
		return lastUpdateFormatter.format(date);
	}
	
	public static Date getFakeOldDate() {
		Date fakeDate = null;
		try {
			return formatter.parse("01/01/1990");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fakeDate;
	}
	
	/**
	 * Returns a string which contains a unique UUID based on actual time 
	 * @return	a string of the UUID
	 */
	public static String getTimeBasedUUID() {
	    return TB_GENERATOR.generate().toString();
	}
	
	/**
	 * Returns a the current date as a <code>String</code> in the format dd/MM/yyyy
	 * @return
	 */
	public static String getCurrentDate() {
		return formatter.format(new Date());
	}
}
