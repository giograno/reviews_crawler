package beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the basic info about an app, like the last update date
 * and the current version
 * @author giograno
 *
 */
public class AppInfo implements Exportable {
	
	private String appName;
	private String lastUpdate;
	private String currentVersion;
	private String category;
	private String numDownloads;
	
	public String getNumDownloads() {
		return numDownloads;
	}

	public void setNumDownloads(String numDownloads) {
		this.numDownloads = numDownloads;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Override
	public List<String> getFieldsToExport() {
		List<String> fields = new ArrayList<String>();
		fields.add(appName);
		fields.add(category);
		fields.add(currentVersion);
		fields.add(lastUpdate);
		fields.add(numDownloads);
		return fields;
	}

}
