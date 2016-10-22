package extractors;

import java.util.ArrayList;

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
	
	public void printNumberOfInputApps() {
		System.out.println(appsToMine.size() + " apps to mine");
	}

}
