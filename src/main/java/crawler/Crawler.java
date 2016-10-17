package crawler;

import java.io.IOException;
import java.util.List;

import beans.Exportable;
import csv.CSVWriter;

public abstract class Crawler implements Runnable { 
	
	/**
	 * Write an <code>Exportable</code> class fields as a CSV line
	 * @param exportable
	 * @throws IOException
	 */
	public void writeLine(Exportable exportable) throws IOException {
		List<String> toWrite = exportable.getFieldsToExport();
		CSVWriter.writeline(toWrite);
	}
	
}

