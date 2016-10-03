package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Formats a <code>String</code> into a <code>Date</code>
	 * @param dateToParse in format dd/MM/yyyy
	 * @return a <code>Date</code>
	 */
	public static Date getDateFromString(String dateToParse) {
		Date date = null;
		
        try {
            date = formatter.parse(dateToParse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return date;
	}
	
	/**
	 * Returns a the current date as a <code>String</code> in the format dd/MM/yyyy
	 * @return
	 */
	public static String getCurrentDate() {
		return formatter.format(new Date());
	}
}
