package utils;

public class ConfigurationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5490155069238251431L;
	
	public ConfigurationException(String element) {
		super("Wrong configuration parameter = " + element);
	}

}
