package io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import config.ConfigurationManager;

/**
 * An utility class used to write the extracted info to a output csv
 * @author giograno
 *
 */
public class CSVWriter {

	private static FileWriter writer = null;
	private static final char DEFAULT_SEPARATOR = ',';
	private static FileWriter appSuccss;
	
	static {
		try {
			writer = new FileWriter(ConfigurationManager.getInstance().getOutputCsv());
			appSuccss = new FileWriter("app_extracted.txt");
		} catch (IOException e) {
			System.err.println("An error occurred while reading the input csv");
		}
	}
	
	/**
	 * Writes a line to the default csv specified by the configuration file
	 * @param linesToWrite
	 * @throws IOException
	 */
	public synchronized static void writeline(List<String> linesToWrite) throws IOException {
		writeLine(linesToWrite, DEFAULT_SEPARATOR);
	}
	
	/**
	 * Writes a line to the default csv; a separator could be specified
	 * @param linesToWrite
	 * @param separator
	 * @throws IOException
	 */
	public synchronized static void writeLine(List<String> linesToWrite, char separator) throws IOException {
		boolean first = true;
		
		StringBuilder builder = new StringBuilder();
		
		for (String line : linesToWrite) {
			if (!first)
				builder.append(separator);
			
			builder.append(followCVSformat(line));
			
			first = false;
 		}
		
		builder.append("\n");
		writer.append(builder.toString());
		writer.flush();
	}
	
	public synchronized static void writeSuccess(String app) {
		try {
			appSuccss.append(app);
			appSuccss.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    private synchronized static String followCVSformat(String value) {

        String result = value;
        
        if (result.contains("\"")) 
            result = result.replace("\"", "\"\"");
        if (result.contains(","))
        	result = result.replace(",", " ");
        
        return result;
    }
}
