package beans;

import java.util.ArrayList;
import java.util.List;

public class AppInfo implements Exportable {
	
	private String appName;
	private String lastUpdate;
	private String currentVersion;

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
		fields.add(currentVersion);
		fields.add(lastUpdate);
		return fields;
	}

}
