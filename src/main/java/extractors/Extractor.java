package extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.Exportable;
import csv.CSVWriter;

/**
 * Generic extractor 
 * @author giograno
 *
 */
public abstract class Extractor {

	protected ArrayList<String> appsToMine;
	
	public Extractor(ArrayList<String> inputApps) {
		this.appsToMine = inputApps;
	}
	
	public abstract void extract();
	
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
