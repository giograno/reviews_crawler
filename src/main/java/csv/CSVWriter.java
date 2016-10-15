package csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import config.ConfigurationManager;

public class CSVWriter {

	private static FileWriter writer = null;
	private static final char DEFAULT_SEPARATOR = ',';
	
	static {
		try {
			writer = new FileWriter(ConfigurationManager.getInstance().getOutputCsv());
		} catch (IOException e) {
			System.err.println("An error occurred while reading the input csv");
		}
	}
	
	public synchronized static void writeline(List<String> linesToWrite) throws IOException {
		writeLine(linesToWrite, DEFAULT_SEPARATOR);
	}
	
	public static void writeLine(List<String> linesToWrite, char separators) throws IOException {
		boolean first = true;
		
		StringBuilder builder = new StringBuilder();
		
		for (String line : linesToWrite) {
			if (!first)
				builder.append(separators);
			
			builder.append(followCVSformat(line));
			
			first = false;
 		}
		
		builder.append("\n");
		writer.append(builder.toString());
		writer.flush();
	}
	
    private static String followCVSformat(String value) {

        String result = value;
        
        if (result.contains("\"")) 
            result = result.replace("\"", "\"\"");
        if (result.contains(","))
        	result = result.replace(",", " ");
        
        return result;
    }
}
