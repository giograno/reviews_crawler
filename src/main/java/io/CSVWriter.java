package io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import beans.Exportable;
import config.ConfigurationManager;

/**
 * An utility class used to write the extracted info to a output csv
 * 
 * @author giograno
 *
 */
public class CSVWriter implements IWriter {

	private FileWriter writer = null;
	private static final char DEFAULT_SEPARATOR = ',';
	private FileWriter appSuccss;
	private static int counter = 0;
	private ConfigurationManager configuration;

	public CSVWriter(ConfigurationManager configuration) {
		try {
			this.configuration = configuration;
			this.writer = new FileWriter(this.configuration.getOutputCsv());
			this.appSuccss = new FileWriter("app_extracted.txt");
		} catch (IOException e) {
			System.err.println("An error occurred while reading the input csv");
		}
	}

	@Override
	public void writeline(Exportable exportable) {
		try {
			writeLine(exportable.getFieldsToExport(), DEFAULT_SEPARATOR);
		} catch (IOException e) {
			System.err.println("An error occurred while performing writing on output");
		}
	}

	/**
	 * Writes a line to the default csv specified by the configuration file
	 * 
	 * @param linesToWrite
	 * @throws IOException
	 */
	public synchronized void writeline(List<String> linesToWrite) throws IOException {
		writeLine(linesToWrite, DEFAULT_SEPARATOR);
	}

	/**
	 * Writes a line to the default csv; a separator could be specified
	 * 
	 * @param linesToWrite
	 * @param separator
	 * @throws IOException
	 */
	public synchronized void writeLine(List<String> linesToWrite, char separator) throws IOException {
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

	public synchronized void writeSuccess(String app) {
		counter++;
		try {
			appSuccss.append(Integer.toString(counter) + ":" + app + "\n");
			appSuccss.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized String followCVSformat(String value) {

		String result = value;

		if (result.contains("\""))
			result = result.replace("\"", "\"\"");
		if (result.contains(","))
			result = result.replace(",", " ");

		return result;
	}
}
